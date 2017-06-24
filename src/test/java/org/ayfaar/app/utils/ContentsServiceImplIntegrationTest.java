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
    private ContentsService service;

    @Test
    public void testGetChildrenForSection() {
        Optional<? extends ContentsService.CategoryProvider> provider = service.getCategory("БДК/Раздел I");
        List<? extends ContentsService.ContentsProvider> children = provider.get().children();

        assertEquals(4, children.size());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), children.get(0).uri());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 4"), children.get(3).uri());
    }

    @Test
    public void testLastParagraph() {
        ContentsService.ParagraphProvider paragraph = service.getParagraph("10.6.3.7").get();
        assertEquals("10.11894", paragraph.from());
        assertEquals("10.11895", paragraph.to());

        paragraph = service.getParagraph("11.8.13.5").get();
        assertEquals("11.13132", paragraph.from());
        assertEquals("11.13133", paragraph.to());

        paragraph = service.getParagraph("13.15.7.24").get();
        assertEquals("13.15546", paragraph.from());
        assertEquals("13.15550", paragraph.to());

        paragraph = service.getParagraph("14.18.7.4").get();
        assertEquals("14.16848", paragraph.from());
        assertEquals("14.16854", paragraph.to());

        paragraph = service.getParagraph("15.22.1.18").get();
        assertEquals("15.17871", paragraph.from());
        assertEquals("15.17876", paragraph.to());
    }

    @Test
    public void testParagraphOrder() {
        final ContentsService.CategoryProvider category = service.getCategory("БДК/Раздел IX/Глава 1").get();

        assertEquals("12.9.1.1", category.children().get(0).code());
        assertEquals("12.9.1.2", category.children().get(1).code());
    }

    @Test
    public void testGetParentForParagraph() {
        Optional<? extends ContentsService.ParagraphProvider> provider = service.getParagraph("10.1.1.1");
        Optional<? extends ContentsService.CategoryProvider> parent = provider.get().parent();

        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), parent.get().uri());
    }

    @Test
    public void testGetParentsFotParagraph() {
        Optional<? extends ContentsService.ParagraphProvider> provider = service.getParagraph("10.1.1.1");
        List<? extends ContentsService.CategoryProvider> parents = provider.get().parents();

        assertEquals(5, parents.size());
        assertEquals(UriGenerator.generate(Category.class, "Содержание"), parents.get(0).uri());
        assertEquals(UriGenerator.generate(Category.class, "БДК"), parents.get(1).uri());
        assertEquals(UriGenerator.generate(Category.class, "Том 10"), parents.get(2).uri());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I"), parents.get(3).uri());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), parents.get(4).uri());
    }

    @Test
    public void testGetProviderByItemNumber() {
        ContentsService.ParagraphProvider provider = service.getByItemNumber("10.10037").get();
        assertEquals(UriGenerator.generate(ItemsRange.class, "10.1.1.4"), provider.uri());
        provider = service.getByItemNumber("14.15639").get();
        assertEquals(UriGenerator.generate(ItemsRange.class, "14.17.6.3"), provider.uri());
        provider = service.getByItemNumber("14.15598").get();
        assertEquals(UriGenerator.generate(ItemsRange.class, "14.16.2.4"), provider.uri());
    }
}
