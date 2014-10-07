package issues.issue63;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.utils.contents.Contents;
import org.hibernate.criterion.MatchMode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import java.util.List;

public class TestContents extends IntegrationTest {
    @Autowired
    Contents contents;

    @Autowired
    CategoryDao categoryDao;

    @Test
    public void testTom() {
        List<Category> root = categoryDao.getTopLevel();
        List<Category> categories = contents.getSubCategories(root.get(0));

        assertEquals(2, categories.size());
        assertEquals("Том 10", categories.get(0).getName());
        assertEquals("Том 14", categories.get(1).getName());
    }

    @Test
    public void testSection() {
        List<Category> toms = categoryDao.getLike("name", "Том 10", MatchMode.END);
        List<Category> categories = contents.getSubCategories(toms.get(0));

        assertEquals(6, categories.size());
        assertEquals("БДК / Раздел I", categories.get(0).getName());
        assertEquals("БДК / Раздел VI", categories.get(5).getName());
    }

    @Test
    public void testChapter() {
        List<Category> sections = categoryDao.getLike("name", "БДК / Раздел I", MatchMode.END);
        List<Category> categories = contents.getSubCategories(sections.get(0));

        assertEquals(4, categories.size());
        assertEquals("БДК / Раздел I / Глава 1", categories.get(0).getName());
        assertEquals("БДК / Раздел I / Глава 4", categories.get(3).getName());
    }

    @Test
    public void testParagraph() {
        List<Category> chapters = categoryDao.getLike("name", "БДК / Раздел I / Глава 1", MatchMode.END);
        List<Category> categories = contents.getSubCategories(chapters.get(0));

        assertEquals(10, categories.size());
        assertEquals("Параграф 10.1.1.1", categories.get(0).getName());
        assertEquals("Параграф 10.1.1.10", categories.get(9).getName());
    }
}
