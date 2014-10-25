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
import static org.junit.Assert.assertEquals;

public class CategoryControllerTest extends IntegrationTest {
    @Autowired
    private ContentsHelper contentsHelper;

    @Test
    public void testGetContentsForTom() {
        CategoryPresentation rootCategory = contentsHelper.createContents("Том 10");
        List<CategoryPresentation> sections = rootCategory.getChildren();
        List<CategoryPresentation> chapters = sections.get(3).getChildren();

        assertEquals("Том 10", rootCategory.getName());
        assertEquals(6, sections.size());
        assertEquals("Раздел I", sections.get(0).getName());
        assertEquals("Раздел V", sections.get(4).getName());
        assertEquals(5, chapters.size());
        assertEquals("Глава 1", chapters.get(0).getName());
        assertEquals("Глава 5", chapters.get(4).getName());
        assertEquals(UriGenerator.generate(Category.class, "БДК / Раздел IV / Глава 5"), chapters.get(4).getUri());
        assertNull(chapters.get(0).getChildren());
    }

    @Test
    public void testGetContentsForSection() {
        CategoryPresentation rootCategory = contentsHelper.createContents("БДК / Раздел III");
        List<CategoryPresentation> chapters = rootCategory.getChildren();
        List<CategoryPresentation> paragraphs = chapters.get(2).getChildren();

        assertEquals("Раздел III", rootCategory.getName());
        assertEquals(7, chapters.size());
        assertEquals("Глава 1", chapters.get(0).getName());
        assertEquals("Глава 7", chapters.get(6).getName());
        assertEquals(9, paragraphs.size());
        assertEquals("Параграф 10.3.3.1", paragraphs.get(0).getName());
        assertEquals("Параграф 10.3.3.9", paragraphs.get(8).getName());
        assertNull(paragraphs.get(0).getChildren());
    }

    @Test
    public void testGetContentsForChapter() {
        String chapterFullName = "БДК / Раздел IV / Глава 3";
        CategoryPresentation rootCategory = contentsHelper.createContents(chapterFullName);
        List<CategoryPresentation> paragraphs = rootCategory.getChildren();
        List<CategoryPresentation> items = paragraphs.get(2).getChildren();

        assertEquals("Глава 3", rootCategory.getName());
        assertEquals(UriGenerator.generate(Category.class, chapterFullName), rootCategory.getUri());
        assertEquals(16, paragraphs.size());
        assertEquals("Параграф 10.4.3.1", paragraphs.get(0).getName());
        assertEquals("Параграф 10.4.3.16", paragraphs.get(15).getName());
        assertEquals(16, paragraphs.size());
        assertEquals("10.11155", items.get(0).getName());
        assertEquals(UriGenerator.generate(Item.class, "10.11155"), items.get(0).getUri());
        assertEquals("10.11170", items.get(15).getName());
        assertNull(items.get(0).getChildren());
    }

    @Test
    public void testGetContentsWithParents() {
        CategoryPresentation rootCategory = contentsHelper.createContents("БДК / Раздел IV / Глава 5");
        List<CategoryPresentation> parents = rootCategory.getParents();
        List<CategoryPresentation> chapterChildren = rootCategory.getChildren();

        assertEquals(3, parents.size());
        assertEquals("Раздел IV", parents.get(0).getName());
        assertEquals("Том 10", parents.get(1).getName());
        assertEquals("БДК", parents.get(2).getName());
        assertNull(parents.get(0).getParents());
        assertNull(parents.get(0).getChildren());
        assertNull(chapterChildren.get(0).getParents());
    }
}
