package org.ayfaar.app.utils;


import org.ayfaar.app.model.Category;


public interface CategoryMap {
    public CategoryProvider getProviderForCategory(String name);
    public CategoryProvider getProviderForItem(String number);
    public Category getCategory(String name);

    public interface CategoryProvider {
        public Category getCategory();
    }
}
