package issues.issue63;


import org.apache.commons.io.IOUtils;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.contents.Contents;
import org.hibernate.criterion.MatchMode;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

public class TestContents extends IntegrationTest {
    @Autowired
    private Contents contents;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;

    private String[] subCategoryTom10;
    private String[] subCategorySectionII;

    @Before
    public void init() throws IOException {
        String tom = IOUtils.toString(this.getClass().getResourceAsStream("subCategoryTom10.txt"));
        String section = IOUtils.toString(this.getClass().getResourceAsStream("subCategorySectionII.txt"));
        subCategoryTom10 = tom.split("\n");
        subCategorySectionII = section.split("\n");
    }

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
        List<Category> toms = categoryDao.getLike("name", "Том 10", MatchMode.END);
        List<Category> categories = contents.getSubCategories(toms.get(0));

        assertEquals(30, categories.size());
        assertEquals("БДК / Раздел I", categories.get(0).getName());
        assertEquals("БДК / Раздел I / Глава 1", categories.get(1).getName());
        assertEquals("БДК / Раздел VI", categories.get(26).getName());
        assertEquals("БДК / Раздел VI / Глава 3", categories.get(29).getName());
    }

    @Test
    public void testGetSubCategoryChapter() {
        List<Category> sections = categoryDao.getLike("name", "БДК / Раздел I", MatchMode.END);
        List<Category> categories = contents.getSubCategories(sections.get(0));

        assertEquals(29, categories.size());
        assertEquals("БДК / Раздел I / Глава 1", categories.get(0).getName());
        assertEquals("Параграф 10.1.1.1", categories.get(1).getName());
        assertEquals("БДК / Раздел I / Глава 2", categories.get(11).getName());
        assertEquals("Параграф 10.1.2.3", categories.get(14).getName());
    }

    @Test
    public void testGetSubCategoryParagraph() {
        List<Category> chapters = categoryDao.getLike("name", "БДК / Раздел I / Глава 1", MatchMode.END);
        List<Category> categories = contents.getSubCategories(chapters.get(0));

        assertEquals(10, categories.size());
        assertEquals("Параграф 10.1.1.1", categories.get(0).getName());
        assertEquals("Параграф 10.1.1.10", categories.get(9).getName());
    }

    @Test
    public void testFormatSectionAndChapter() {
        String expectedSection = "Раздел II. Новейшие духовно-космологические Представления о Вселенной и о человеке";
        String expectedChapter = "Глава 2. Понятия «Фокусов»";
        List<Category> sections = categoryDao.getLike("name", "БДК / Раздел II", MatchMode.END);
        List<Category> chapters = categoryDao.getLike("name", "БДК / Раздел I / Глава 2", MatchMode.END);
        String section = contents.formatSectionAndChapter(sections.get(0));
        String chapter = contents.formatSectionAndChapter(chapters.get(0));

        assertEquals(expectedSection, section);
        assertEquals(expectedChapter, chapter);
    }

    @Test
    public void testFormatParagraph() {
        String expectedParagraph = "§10.1.1.1 О бесконечности Мироздания и Ииссиидиологии, которая призвана в" +
                " корне изменить  наши взгляды";
        List<Category> paragraphs = categoryDao.getLike("name", "Параграф 10.1.1.1", MatchMode.END);
        String paragraph = contents.formatParagraph(paragraphs.get(0));

        assertEquals(expectedParagraph, paragraph);
    }

    @Test
    public void testFormatItem() {
        String expectedItem = "10.10488. В этом сложном и одновременном эволюционно-инволюционном Процессе" +
                " есть свои особенности и тонкости, первой из которых является наличие принципа большей степени" +
                " совместимости, избирательности или предпочтительности при инерционном осуществлении Синтеза одних" +
                " Аспектов Качеств по отношению к Аспектам других Качеств.";
        List<Item> items = itemDao.getLike("number", "10.10488", MatchMode.END);
        String item = contents.formatItem(items.get(0));

        assertEquals(expectedItem, item);
    }

    @Test
    public void testCreateContents() {
        List<Category> toms = categoryDao.getLike("name", "Том 10", MatchMode.END);
        List<Category> sections = categoryDao.getLike("name", "БДК / Раздел II", MatchMode.END);
        List<Category> chapters = categoryDao.getLike("name", "БДК / Раздел I / Глава 1", MatchMode.END);
        List<String> tom10Contents = contents.createContents(toms.get(0));
        List<String> sectionIIContents = contents.createContents(sections.get(0));
        List<String> chapterContents = contents.createContents(chapters.get(0));

        assertEquals(30, tom10Contents.size());
        assertEquals(subCategoryTom10[0].trim(), tom10Contents.get(0));
        assertEquals(subCategoryTom10[10].trim(), tom10Contents.get(10));
        assertEquals(subCategoryTom10[29].trim(), tom10Contents.get(29));
        assertEquals(8, sectionIIContents.size());
        assertEquals(subCategorySectionII[0].trim(), sectionIIContents.get(0));
        assertEquals(subCategorySectionII[1].trim(), sectionIIContents.get(1));
        assertEquals(subCategorySectionII[7].trim(), sectionIIContents.get(7));
        assertEquals(117, chapterContents.size());
    }
}
