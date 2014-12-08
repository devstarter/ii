package org.ayfaar.app.utils;


import org.ayfaar.app.model.Category;

import java.util.List;


public interface CategoryMap {
    public CategoryProvider getProviderForCategory(String name);
    public CategoryProvider getProviderByItemNumber(String number);
    public Category getCategory(String name);

    public interface CategoryProvider {
        public Category getCategory();
        public CategoryProvider getNext();
        public String getUri();
        public String getParentUri();
        public String getDescription();
        public boolean isParagraph();
        public boolean isTom();
        public boolean isCikl();
        public List<CategoryProvider> getChildren();
        public CategoryProvider getParent();
        public List<CategoryProvider> getParents();
        public String extractCategoryName();
    }
}
