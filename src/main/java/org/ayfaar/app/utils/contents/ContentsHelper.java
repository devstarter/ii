package org.ayfaar.app.utils.contents;

import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.utils.StringUtils.trim;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class ContentsHelper {
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;

    private final int SUBCATEGORY_COUNT = 2;

    public CategoryPresentation createContents(String categoryName) {
        Category category = categoryDao.get("name", categoryName);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }
        Category previous = categoryDao.get("next", UriGenerator.generate(Category.class, categoryName));

        String previousCategory = previous != null ? previous.getName() : null;
        String nextCategory = category.getNext() != null ? getValueFromUri(Category.class, category.getNext()) : null;

        if(category.isParagraph()) {
            return new CategoryPresentation(category.getName(), category.getUri(),
                    trim(category.getDescription()), previousCategory, nextCategory,
                    createParentPresentation(getParents(category)),
                    getParagraphSubCategory(getItems(category), 1));
        } else {
            return new CategoryPresentation(extractCategoryName(category.getName()), category.getUri(),
                    category.getDescription(), previousCategory, nextCategory,
                    createParentPresentation(getParents(category)),
                    createChildrenPresentation(getChildren(category), 0));
        }
    }

    private List<CategoryPresentation> createChildrenPresentation(List<Category> categories, int count) {
        if(count >= SUBCATEGORY_COUNT) return null;

        List<CategoryPresentation> childrenPresentations = new ArrayList<CategoryPresentation>();

        count++;

        for (Category category : categories) {
            if (!category.isParagraph()) {
                childrenPresentations.add(new CategoryPresentation(
                        extractCategoryName(category.getName()), category.getUri(), category.getDescription(),
                        createChildrenPresentation(getChildren(category), count)));

            } else {
                List<Item> items = getItems(category);
                CategoryPresentation presentation = new CategoryPresentation(category.getName(),
                        category.getUri(), trim(category.getDescription()), getParagraphSubCategory(items, count));

                childrenPresentations.add(presentation);
            }
        }

        return childrenPresentations;
    }

    private List<CategoryPresentation> getParagraphSubCategory(List<Item> items, int count) {
        if(count >= SUBCATEGORY_COUNT) return null;
        List<CategoryPresentation> listPresentations = new ArrayList<CategoryPresentation>();
        for (Item item : items) {
            listPresentations.add(new CategoryPresentation(item.getNumber(), item.getUri()));
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

    /**
     * For parents presentations we need only name, uri and description. children and parents should be null
     *
     * @param categories
     * @return
     */
    List<CategoryPresentation> createParentPresentation(List<Category> categories) {
        List<CategoryPresentation> presentations = new ArrayList<CategoryPresentation>();

        for(Category category : categories) {
            presentations.add(new CategoryPresentation(extractCategoryName(category.getName()),
                    category.getUri(), trim(category.getDescription())));
        }
        return presentations;
    }
}
