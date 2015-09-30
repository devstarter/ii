package org.ayfaar.app.utils;


import org.ayfaar.app.model.Category;

import java.util.List;


public interface CategoryMap {
    CategoryProvider getProviderForCategoryName(String name);
    CategoryProvider getProviderByItemNumber(String number);
    Category getCategory(String name);
    List<CategoryProvider> descriptionContains(List<String> searchQueries);
    void reload();

    interface CategoryProvider {
        Category getCategory();
        CategoryProvider getNext();
        String getUri();
        String getParentUri();
        String getDescription();
        boolean isParagraph();
        boolean isTom();
        boolean isCikl();
        boolean isContentsRoot();
        List<CategoryProvider> getChildren();
        CategoryProvider getParent();
        List<CategoryProvider> getParents();
        String extractCategoryName();
        String getPath();
    }
}
