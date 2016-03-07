package org.ayfaar.app.utils;


import org.ayfaar.app.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryProvider getByName(String name);
    CategoryProvider getByItemNumber(String number);
    List<CategoryProvider> descriptionContains(List<String> searchQueries);
    void reload();

	CategoryProvider getByUri(String uri);

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
        String getCode();
		CategoryProvider getPrevious();
		String getPreviousUri();
		String getNextUri();
        String getStartItemNumber();
    }
}
