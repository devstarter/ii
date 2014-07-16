package issues.issue2;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsCleaner;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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


    //clean DB of extra chapters and sections
    @Test
    @Ignore
    public void cleanAllDB() {
        ItemsCleaner.cleanDB(itemDao);
    }

    @Test
    public void isNotContainChapter() {
        String wrongValue = "Глава";
        // Здесь можно воспользоваться методом itemDao.getLike(..) чтобы не загружать все пункты,
        // а отфильтроваит их на этапе выборки из базы данных - экономия памяти
        List<Item> items = itemDao.getAll();

        for(Item item : items) {
            assertFalse("Item "+item.getNumber()+" has "+wrongValue, isContain(item.getContent(), wrongValue));
        }
    }

    @Test
    public void isNotContainSection() {
        String wrongValue = "РАЗДЕЛ";
        // аналогично
        List<Item> items = itemDao.getAll();

        for(Item item : items) {
            assertFalse("Item "+item.getNumber()+" has "+wrongValue, isContainIgnoreCase(item.getContent(), wrongValue));
        }
    }

    public boolean isContain(String itemContext, String value) {
        return itemContext.contains(value);
    }
    public boolean isContainIgnoreCase(String itemContext, String value) {
        // todo: implement it
        throw new NotImplementedException();
    }
}
