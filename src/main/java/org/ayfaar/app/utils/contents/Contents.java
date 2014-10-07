package org.ayfaar.app.utils.contents;

import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Contents {
    @Autowired
    private CategoryDao categoryDao;

    public List<Category> getSubCategories(Category category) {
        List<Category> categories = getChildren(category);

        print(categories);

        return categories;
    }



    private void print(List<Category> categories) {
        for(Category c : categories) {
            System.out.println(c.getName());
        }
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
}
