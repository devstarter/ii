package org.ayfaar.app.utils;

import org.ayfaar.app.model.Category;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


public class CategoryMapImpl implements CategoryMap {
    private Map<String, CategoryProvider> categoryMap;

    @PostConstruct
    public void load() {
        categoryMap = new HashMap<String, CategoryProvider>();
    }

    @Override
    public CategoryProvider getCategoryProvider(String name) {
        return null;
    }

    @Override
    public Category getCategory(String name) {
        return null;
    }

    public class CategoryProviderImpl implements CategoryProvider {
        private String parentName;

        @Override
        public Category getCategory() {
            return null;
        }
    }
}
