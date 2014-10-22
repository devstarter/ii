package org.ayfaar.app.utils.contents;

import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ayfaar.app.utils.StringUtils.trim;

@Component
public class ContentsHelper {
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;

    private final int SUBCATEGORY_COUNT = 3;
    private int countSubCategory = 0;
    private int countParentsChildren = 0;

    public List<CategoryPresentation> createContents(String categoryName) {
        Category category = categoryDao.get("name", categoryName);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        List<CategoryPresentation> content = createChildrenPresentation(Arrays.asList(category), countSubCategory);
        List<CategoryPresentation> parents = createParentPresentation(getParents(category), countParentsChildren);
        content.get(0).setParents(parents);
        return content;
    }

    private List<CategoryPresentation> createChildrenPresentation(List<Category> parents, int count) {
        List<CategoryPresentation> listPresentations = new ArrayList<CategoryPresentation>();

        if(count < SUBCATEGORY_COUNT) {
            count++;
            for (Category category : parents) {
                if (!category.isParagraph()) {
                    String description = category.isTom() ? "" : category.getDescription();
                    List<Category> children = getChildren(category);

                    CategoryPresentation presentation = new CategoryPresentation(
                            extractCategoryName(category.getName()), category.getUri(), description,
                            createChildrenPresentation(children, count));

                    listPresentations.add(presentation);
                } else {
                    List<Item> items = getItems(category);
                    CategoryPresentation presentation = new CategoryPresentation(category.getName(),
                            category.getUri(), trim(category.getDescription()), getParagraphSubCategory(items, count));

                    listPresentations.add(presentation);
                }
            }
        }
        return listPresentations;
    }

    private List<CategoryPresentation> getParagraphSubCategory(List<Item> items, int count) {
        List<CategoryPresentation> listPresentations = new ArrayList<CategoryPresentation>();
        if(count < SUBCATEGORY_COUNT) {
            for (Item item : items) {
                listPresentations.add(new CategoryPresentation(item.getNumber(), item.getUri(), "", null));
            }
        }
        return listPresentations;
    }

    List<Category> getChildren(Category parent) {
        List<Category> children = new ArrayList<Category>();
        if (parent.getStart() != null) {
            Category child = categoryDao.get(parent.getStart());
            if (child != null) {
                children.add(child);
                while (child.getNext() != null) {
                    child = categoryDao.get(child.getNext());
                    if (!child.getParent().equals(parent.getUri()))
                        break;
                    children.add(child);
                }
            }
        }
        return children;
    }

    List<Category> getParents(Category category) {
        List<Category> parents = new ArrayList<Category>();
        if(category.getParent() != null) {
            Category c = categoryDao.get(category.getParent());
            parents.add(c);
            parents.addAll(getParents(c));
        }
        return parents;
    }

    List<Item> getItems(Category category) {
        List<Item> items = new ArrayList<Item>();
        Item currentItem = itemDao.get(category.getStart());

        String itemNumber = currentItem.getNumber();
        String endNumber = category.getEnd() != null ? itemDao.get(category.getEnd()).getNumber() : itemNumber;

        items.add(currentItem);
        while(!itemNumber.equals(endNumber)) {
            itemNumber = ItemController.getNext(itemNumber);
            currentItem = itemDao.getByNumber(itemNumber);
            items.add(currentItem);
        }
        return items;
    }

    String extractCategoryName(String categoryName) {
        String[] names = categoryName.split("/");
        return names[names.length-1].trim();
    }

    List<CategoryPresentation> createParentPresentation(List<Category> categories, int count) {
        List<CategoryPresentation> presentations = new ArrayList<CategoryPresentation>();
        count++;

        for(Category category : categories) {
            if(!category.isParagraph()) {
                String description = category.isTom() ? "" : category.getDescription();
                List<Category> parents = getParents(category);
                List<Category> children = getChildren(category);
                CategoryPresentation presentation = new CategoryPresentation(extractCategoryName(category.getName()),
                        category.getUri(), description, createParentPresentation(parents, count),
                        createChildrenPresentation(children, count));

                presentations.add(presentation);
            } else {
                List<Category> par = getParents(category);
                List<Item> items = getItems(category);
                CategoryPresentation presentation = new CategoryPresentation(category.getName(),
                        category.getUri(), trim(category.getDescription()),
                        createParentPresentation(par, count), getParagraphSubCategory(items, count));

                presentations.add(presentation);
            }
        }
        return presentations;
    }
}
