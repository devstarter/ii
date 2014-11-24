package org.ayfaar.app.utils;


import org.ayfaar.app.model.Category;

import java.util.List;


public interface CategoryMap {
    public CategoryProvider getProviderForCategory(String name);

    /**
     * Возможно категории не нужны вообще, для построения содержания должно хватить провайдеров
     */
    public Category getCategory(String name);

    /**
     * Не уверен нужен ли тут этот метод или выполнять это в ContentsHelper
     */
    public List<CategoryProvider> getParents(String name);

    public interface CategoryProvider {
        public Category getCategory();
        public String getUri();
        public String getParentUri();
        public String getNext();
        public String getStart();
        public List<CategoryProvider> getChildren();
        public CategoryProvider getParent();
    }
}
