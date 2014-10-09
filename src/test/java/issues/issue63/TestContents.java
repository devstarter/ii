package issues.issue63;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.contents.Contents;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import java.util.List;

public class TestContents extends IntegrationTest {
    @Autowired
    private Contents contents;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;

    @Test
    public void testGetSubCategoryTom() {
        List<Category> root = categoryDao.getTopLevel();
        List<Category> categories = contents.getSubCategories(root.get(0));

        assertEquals(11, categories.size());
        assertEquals("Том 10", categories.get(0).getName());
        assertEquals("БДК / Раздел I", categories.get(1).getName());
        assertEquals("Том 14", categories.get(7).getName());
        assertEquals("БДК / Раздел ХVIII", categories.get(10).getName());
    }

    @Test
    public void testGetSubCategorySection() {
        Category tom = categoryDao.get("name", "Том 10");
        List<Category> categories = contents.getSubCategories(tom);

        assertEquals(30, categories.size());
        assertEquals("БДК / Раздел I", categories.get(0).getName());
        assertEquals("БДК / Раздел I / Глава 1", categories.get(1).getName());
        assertEquals("БДК / Раздел VI", categories.get(26).getName());
        assertEquals("БДК / Раздел VI / Глава 3", categories.get(29).getName());
    }

    @Test
    public void testGetSubCategoryChapter() {
        Category section = categoryDao.get("name", "БДК / Раздел I");
        List<Category> categories = contents.getSubCategories(section);

        assertEquals(29, categories.size());
        assertEquals("БДК / Раздел I / Глава 1", categories.get(0).getName());
        assertEquals("Параграф 10.1.1.1", categories.get(1).getName());
        assertEquals("БДК / Раздел I / Глава 2", categories.get(11).getName());
        assertEquals("Параграф 10.1.2.3", categories.get(14).getName());
    }

    @Test
    public void testGetSubCategoryParagraph() {
        Category chapter = categoryDao.get("name", "БДК / Раздел I / Глава 1");
        List<Category> categories = contents.getSubCategories(chapter);

        assertEquals(10, categories.size());
        assertEquals("Параграф 10.1.1.1", categories.get(0).getName());
        assertEquals("Параграф 10.1.1.10", categories.get(9).getName());
    }

    @Test
    public void testFormatSectionAndChapter() {
        String expectedSection = "Раздел II. Новейшие духовно-космологические Представления о Вселенной и о человеке";
        String expectedChapter = "Глава 2. Понятия «Фокусов»";
        Category section = categoryDao.get("name", "БДК / Раздел II");
        Category chapter = categoryDao.get("name", "БДК / Раздел I / Глава 2");
        String formattedSection = contents.formatSectionAndChapter(section);
        String formattedChapter = contents.formatSectionAndChapter(chapter);

        assertEquals(expectedSection, formattedSection);
        assertEquals(expectedChapter, formattedChapter);
    }

    @Test
    public void testFormatParagraph() {
        String expectedParagraph = "§10.1.1.1 О бесконечности Мироздания и Ииссиидиологии, которая призвана в" +
                " корне изменить  наши взгляды";
        Category paragraph = categoryDao.get("name", "Параграф 10.1.1.1");
        String formattedParagraph = contents.formatParagraph(paragraph);

        assertEquals(expectedParagraph, formattedParagraph);
    }

    @Test
    public void testFormatItem() {
        String expectedItem = "10.10488. В этом сложном и одновременном эволюционно-инволюционном Процессе" +
                " есть свои особенности и тонкости, первой из которых является наличие принципа большей степени" +
                " совместимости, избирательности или предпочтительности при инерционном осуществлении Синтеза одних" +
                " Аспектов Качеств по отношению к Аспектам других Качеств.";
        Item item = itemDao.get("number", "10.10488");
        String formattedItem = contents.formatItem(item);

        assertEquals(expectedItem, formattedItem);
    }
}
