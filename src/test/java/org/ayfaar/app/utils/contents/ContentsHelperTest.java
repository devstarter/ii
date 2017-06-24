package org.ayfaar.app.utils.contents;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CategoryDao;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@Ignore
public class ContentsHelperTest extends IntegrationTest {
    @Autowired
    private ContentsHelper contentsHelper;

    @Autowired
    private CategoryDao categoryDao;

/*    @Test
    public void testGetChildren() {
        List<Category> sections = contentsHelper.children(categoryDao.get("name", "Том 10"));
        List<Category> chapters = contentsHelper.children(categoryDao.get("name", "БДК / Раздел I"));
        List<Category> paragraphs = contentsHelper.children(categoryDao.get("name", "БДК / Раздел I / Глава 1"));
        assertEquals(6, sections.size());
        assertEquals(4, chapters.size());
        assertEquals(10, paragraphs.size());
    }

    @Test
    public void testGetItems() {
        Category category = categoryDao.get("name", "Параграф 10.1.1.6");
        List<Item> items = contentsHelper.getItems(category);
        assertEquals(15, items.size());
        assertEquals("10.10045", items.get(0).getNumber());
        assertEquals("10.10059", items.get(14).getNumber());
    }*/

    @Test
    public void testExtractCategoryName() {
        assertEquals("Раздел I", contentsHelper.extractCategoryName("БДК / Раздел I"));
        assertEquals("Глава 5", contentsHelper.extractCategoryName("БДК / Раздел IV / Глава 5"));
    }

/*    @Test
    public void testGetParents() {
        Category category = categoryDao.get("name", "Параграф 10.1.1.6");
        List<Category> parents = contentsHelper.parents(category);
        List<CategoryPresentation> parentPresentations = contentsHelper.createParentPresentation(parents);

        assertEquals(4, parentPresentations.size());
        assertEquals("Глава 1", parentPresentations.get(0).getName());
        assertNull(parentPresentations.get(0).children());
        assertNull(parentPresentations.get(0).parents());
        assertEquals("Раздел I", parentPresentations.get(1).getName());
        assertEquals("Том 10", parentPresentations.get(2).getName());
        assertEquals("БДК", parentPresentations.get(3).getName());
    }*/
}
