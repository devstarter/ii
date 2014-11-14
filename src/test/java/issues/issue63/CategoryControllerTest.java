package issues.issue63;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Category;
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
    public void testCreateContentsForTom() {
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
    public void testCreateContentsForSection() {
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
    public void testCreateContentsForChapter() {
        String chapterFullName = "БДК / Раздел IV / Глава 3";
        CategoryPresentation rootCategory = contentsHelper.createContents(chapterFullName);
        List<CategoryPresentation> paragraphs = rootCategory.getChildren();

        assertEquals("Глава 3", rootCategory.getName());
        assertEquals(UriGenerator.generate(Category.class, chapterFullName), rootCategory.getUri());
        assertEquals(16, paragraphs.size());
        assertEquals("Параграф 10.4.3.1", paragraphs.get(0).getName());
        assertEquals("Параграф 10.4.3.16", paragraphs.get(15).getName());
        assertEquals(16, paragraphs.size());
        assertNull(paragraphs.get(0).getChildren());
    }

    @Test
    public void testCreateContentsForParagraph() {
        String paragraphName = "Параграф 10.1.2.3";
        CategoryPresentation presentation = contentsHelper.createContents(paragraphName);
        List<CategoryPresentation> items = presentation.getChildren();

        assertEquals("Параграф 10.1.2.3", presentation.getName());
        assertEquals(UriGenerator.generate(Category.class, paragraphName), presentation.getUri());
        assertNotNull(presentation.getChildren());
        assertEquals(10, items.size());
        assertEquals("10.10131", items.get(0).getName());
        assertEquals("10.10140", items.get(9).getName());
        assertEquals("Параграф 10.1.3.1", presentation.getNext());
        assertEquals("Параграф 10.1.2.2", presentation.getPrevious());
        assertNotNull(presentation.getParents());
    }

    @Test
    public void testCreateContentsWithParents() {
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

    @Test
    public void testNextAndPreviousCategory() {
        CategoryPresentation rootCategory = contentsHelper.createContents("БДК / Раздел III / Глава 7");
        assertEquals("БДК / Раздел III / Глава 6", rootCategory.getPrevious());
        assertEquals("БДК / Раздел IV / Глава 1", rootCategory.getNext());
    }

    @Test
    public void testCreateContentsWhenNextOrPreviousCategoryIsNull() {
        CategoryPresentation tom10Content = contentsHelper.createContents("Том 10");
        CategoryPresentation tom14Content = contentsHelper.createContents("Том 14");
        CategoryPresentation section = contentsHelper.createContents("БДК / Раздел ХVIII");

        assertNull(tom10Content.getPrevious());
        assertEquals("Том 14", tom10Content.getNext());
        assertEquals("Том 10", tom14Content.getPrevious());
        assertNull(tom14Content.getNext());
        assertNull(section.getNext());
    }
}
