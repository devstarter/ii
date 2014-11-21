package org.ayfaar.app.utils;


import org.ayfaar.app.model.Category;


public interface CategoryMap {
    public CategoryProvider getCategoryProvider(String name);
    public Category getCategory(String name);

    public interface CategoryProvider {
        public Category getCategory();
    }
}
