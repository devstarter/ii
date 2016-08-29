package org.ayfaar.app.utils;


import one.util.streamex.StreamEx;
import org.ayfaar.app.model.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContentsService {
    Optional<? extends ContentsProvider> get(String name);
    Optional<? extends CategoryProvider> getCategory(String name);
    Optional<? extends ParagraphProvider> getParagraph(String name);
    Optional<? extends ParagraphProvider> getByItemNumber(String number);
    List<ContentsProvider> descriptionContains(List<String> searchQueries);
    void reload();

    Optional<? extends ContentsProvider> getByUri(String uri);

    StreamEx<? extends CategoryProvider> getAllCategories();

    StreamEx<? extends ParagraphProvider> getAllParagraphs();

    Map<String, String> getAllUriNames();
    Map<String, String> getAllUriDescription();

    interface CategoryProvider extends ContentsProvider {
        Category getCategory();
        String getParentUri();
        //        boolean isParagraph();
        boolean isTom();
        boolean isCikl();
        boolean isContentsRoot();
        List<? extends ContentsProvider> children();
        String extractCategoryName();
        CategoryProvider getParent();


        Optional<? extends ContentsProvider> getPrevious();

        Optional<? extends CategoryProvider> next();
    }

    interface ParagraphProvider extends ContentsProvider {
        Optional<? extends ParagraphProvider> previous();
        Optional<? extends CategoryProvider> parent();

        String from();
        String to();
    }

    interface ContentsProvider {
        String description();
		Optional<String> previousUri();
		Optional<String> nextUri();
        String code();
        String uri();
        String startItemNumber();
        String name();
        Optional<? extends ContentsProvider> next();
        List<? extends CategoryProvider> parents();
        String path();
    }
}
