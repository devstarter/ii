package org.ayfaar.app.utils;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.ItemsRange;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ContentsServiceImplIntegrationTest extends IntegrationTest{
    @Autowired
    private ContentsService categoryMap;

    @Test
    public void testGetChildrenForSection() {
        Optional<? extends ContentsService.CategoryProvider> provider = categoryMap.getCategory("БДК/Раздел I");
        List<? extends ContentsService.ContentsProvider> children = provider.get().getChildren();

        assertEquals(4, children.size());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), children.get(0).uri());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 4"), children.get(3).uri());
    }

    @Test
    public void testGetParentForParagraph() {
        Optional<? extends ContentsService.ParagraphProvider> provider = categoryMap.getParagraph("10.1.1.1");
        Optional<? extends ContentsService.CategoryProvider> parent = provider.get().parent();

        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), parent.get().uri());
    }

    @Test
    public void testGetParentsFotParagraph() {
        Optional<? extends ContentsService.ParagraphProvider> provider = categoryMap.getParagraph("10.1.1.1");
        List<? extends ContentsService.CategoryProvider> parents = provider.get().parents();

        assertEquals(5, parents.size());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), parents.get(0).uri());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I"), parents.get(1).uri());
        assertEquals(UriGenerator.generate(Category.class, "Том 10"), parents.get(2).uri());
        assertEquals(UriGenerator.generate(Category.class, "БДК"), parents.get(3).uri());
        assertEquals(UriGenerator.generate(Category.class, "Содержание"), parents.get(4).uri());
    }

    @Test
    public void testGetProviderByItemNumber() {
        ContentsService.ParagraphProvider provider = categoryMap.getByItemNumber("10.10037").get();
        assertEquals(UriGenerator.generate(ItemsRange.class, "10.1.1.4"), provider.uri());
        provider = categoryMap.getByItemNumber("14.15639").get();
        assertEquals(UriGenerator.generate(ItemsRange.class, "14.16.2.14"), provider.uri());
        provider = categoryMap.getByItemNumber("14.15598").get();
        assertEquals(UriGenerator.generate(ItemsRange.class, "14.16.2.4"), provider.uri());
    }
}
