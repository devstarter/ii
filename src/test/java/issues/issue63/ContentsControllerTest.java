package issues.issue63;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.ayfaar.app.utils.contents.ContentsHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ContentsControllerTest extends IntegrationTest {
    @Autowired
    private ContentsHelper contentsHelper;

    @Test
    public void testGetContentsForTom() {
        List<CategoryPresentation> contents = contentsHelper.createContents("Том 10");
        List<CategoryPresentation> sections = contents.get(0).getChildren();
        List<CategoryPresentation> chapters = sections.get(3).getChildren();

        assertEquals(1, contents.size());
        assertTrue(contents.get(0).getDescription().isEmpty());
        assertEquals("Том 10", contents.get(0).getName());
        assertEquals(6, sections.size());
        assertEquals("Раздел I", sections.get(0).getName());
        assertEquals("Раздел V", sections.get(4).getName());
        assertEquals(5, chapters.size());
        assertEquals("Глава 1", chapters.get(0).getName());
        assertEquals("Глава 5", chapters.get(4).getName());
        assertEquals(UriGenerator.generate(Category.class, "БДК / Раздел IV / Глава 5"), chapters.get(4).getUri());
        assertTrue(chapters.get(0).getChildren().isEmpty());
    }

    @Test
    public void testGetContentsForSection() {
        List<CategoryPresentation> contents = contentsHelper.createContents("БДК / Раздел III");
        List<CategoryPresentation> chapters = contents.get(0).getChildren();
        List<CategoryPresentation> paragraphs = chapters.get(2).getChildren();

        assertEquals(1, contents.size());
        assertEquals("Раздел III", contents.get(0).getName());
        assertEquals(7, chapters.size());
        assertEquals("Глава 1", chapters.get(0).getName());
        assertEquals("Глава 7", chapters.get(6).getName());
        assertEquals(9, paragraphs.size());
        assertEquals("Параграф 10.3.3.1", paragraphs.get(0).getName());
        assertEquals("Параграф 10.3.3.9", paragraphs.get(8).getName());
        assertTrue(paragraphs.get(0).getChildren().isEmpty());
    }

    @Test
    public void testGetContentsForChapter() {
        List<CategoryPresentation> contents = contentsHelper.createContents("БДК / Раздел IV / Глава 3");
        List<CategoryPresentation> paragraphs = contents.get(0).getChildren();
        List<CategoryPresentation> items = paragraphs.get(2).getChildren();

        assertEquals(1, contents.size());
        assertEquals("Глава 3", contents.get(0).getName());
        assertEquals(16, paragraphs.size());
        assertEquals("Параграф 10.4.3.1", paragraphs.get(0).getName());
        assertEquals("Параграф 10.4.3.16", paragraphs.get(15).getName());
        assertEquals(16, paragraphs.size());
        assertEquals("10.11155", items.get(0).getName());
        assertEquals(UriGenerator.generate(Item.class, "10.11155"), items.get(0).getUri());
        assertEquals("10.11170", items.get(15).getName());
        assertNull(items.get(0).getChildren());
    }
}
