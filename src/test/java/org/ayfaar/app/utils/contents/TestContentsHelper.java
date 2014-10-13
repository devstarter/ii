package org.ayfaar.app.utils.contents;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestContentsHelper extends IntegrationTest {
    @Autowired
    private ContentsHelper contentsHelper;
    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void testGetChildren() {
        List<Category> sections = contentsHelper.getChildren(categoryDao.get("name", "Том 10"));
        List<Category> chapters = contentsHelper.getChildren(categoryDao.get("name", "БДК / Раздел I"));
        List<Category> paragraphs = contentsHelper.getChildren(categoryDao.get("name", "БДК / Раздел I / Глава 1"));
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
    }

    @Test
    public void testCreateContents() {
        //List<CategoryPresentation> chapters= contentsHelper.createContents("БДК / Раздел I");
        //List<CategoryPresentation> paragraphs = contentsHelper.createContents("БДК / Раздел I / Глава 1");
        //List<CategoryPresentation> items = contentsHelper.createContents("Параграф 10.1.1.1");
        List<CategoryPresentation> sections = contentsHelper.createContents("Том 10");

        for(CategoryPresentation c : sections) {
            System.out.println(c.getName() + " "  +c.getDescription() + c.getChildren());
        }
    }
}
