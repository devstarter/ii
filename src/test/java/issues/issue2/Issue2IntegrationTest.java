package issues.issue2;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsCleaner;
import org.hibernate.criterion.MatchMode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Issue2IntegrationTest extends IntegrationTest {

    @Value("#{T(org.apache.commons.io.FileUtils).readFileToString(" +
            "T(org.springframework.util.ResourceUtils).getFile('classpath:issues/issue2/clean-item-3.0089.txt')" +
            ")}")
    String itemExpectedContent;

    @Autowired ItemDao itemDao;

    @Test
    public void checkParticularItem() {
        Item item = itemDao.getByNumber("3.0089");
        assertEquals(itemExpectedContent, item.getContent());
    }


    @Test
    public void isNotContainChapter() {
        String wrongValue = "\nГлава";

        List<Item> items = itemDao.getLike("content", wrongValue, MatchMode.ANYWHERE);
        assertTrue("Items contain " + items.size() + " elements ",  items.isEmpty());
    }

    @Test
     public void isNotContainSection1() {
        String wrongValue = "\nРаздел";

        List<Item> items = itemDao.getLike("content", wrongValue, MatchMode.ANYWHERE);
        assertTrue("Items contain " + items.size() + " elements ",  items.isEmpty());
    }

    @Test
    public void isNotContainSection2() {
        String wrongValue = "РАЗДЕЛ\n";

        List<Item> items = itemDao.getLike("content", wrongValue, MatchMode.ANYWHERE);
        assertTrue("Items contain " + items.size() + " elements ",  items.isEmpty());
    }

    /**
     * Метод для единоразовой очистки базы данных
     * clean DB of extra chapters and sections
     */
//    @Test
    public void cleanDBFromChaptersAndSections() {
        cleanDB(itemDao);
    }

    private void cleanDB(ItemDao dao) {
        List<Item> items = dao.getAll();
        for(Item item : items) {
            String clean = ItemsCleaner.clean(item.getContent());
            if (!clean.equals(item.getContent())) {
                item.setContent(clean);
                dao.save(item);
            }
        }
    }
}
