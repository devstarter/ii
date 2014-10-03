package org.ayfaar.app.utils.contents;


import org.ayfaar.app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class Contents {
    private List<Category> contents = new ArrayList<Category>();

    public List<Category> getContents(Category category) {
        List<Category> children = new ArrayList<Category>();
        children = getChildren(category);

        for(Category c : children) {
            decorateChildren(c);
        }
        return children;
    }

    private List<Category> getChildren(Category parent) {
        //ищем все дочерние категории
        List<Category> children = new ArrayList<Category>();
        return  children;
    }

    private Category decorateChildren(Category children) {

        FormatContent formatContent = new FormatContent();
        formatContent.formatCategory(children);



        //return changed children category;
        return null;
    }
}
