package org.ayfaar.app.utils.contents;

import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Contents {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ItemDao itemDao;

    public List<String> createContents(Category category) {
        List<Category> subCategories = getSubCategories(category);
        List<String> contents = new ArrayList<String>();

        if(subCategories.get(0).isParagraph()) {
            for(Category c : subCategories) {
                contents.add(formatParagraph(c));
                contents.addAll(getItems(c));
            }
        }
        else {
            for(Category c : subCategories) {
                if(c.isParagraph()) {
                    contents.add(formatParagraph(c));
                }
                else {
                    contents.add(formatSectionAndChapter(c));
                }
            }
        }

        return contents;
    }

    public List<Category> getSubCategories(Category category) {
        List<Category> categories = getChildren(category);
        List<Category> subCategories = new ArrayList<Category>();

        for(Category c : categories) {
            subCategories.add(c);
            subCategories.addAll(getChildren(c));
        }

        return subCategories;
    }

    private List<Category> getChildren(Category parent) {
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

    private List<String> getItems(Category category) {
        List<String> itemContents = new ArrayList<String>();

        Item currentItem = itemDao.get(category.getStart());
        String itemNumber = currentItem.getNumber();
        String endNumber = null;
        if (category.getEnd() != null) {
            endNumber = itemDao.get(category.getEnd()).getNumber();
        }

        itemContents.add(formatItem(currentItem));
        while(!itemNumber.equals(endNumber)) {
            itemNumber = ItemController.getNext(itemNumber);
            currentItem = itemDao.getByNumber(itemNumber);
            itemContents.add(formatItem(currentItem));
        }

        return itemContents;
    }

    public String formatSectionAndChapter(Category category) {
        String[] names = category.getName().split("/");
        String line = names[names.length-1].trim() + ". " + category.getDescription();
        return  line;
    }

    public String formatParagraph(Category category) {
        return  category.getName().replace("Параграф ", "§") + " " + category.getDescription();
    }

    public String formatItem(Item item) {
        return item.getNumber() + ". " + item.getContent();
    }
}
