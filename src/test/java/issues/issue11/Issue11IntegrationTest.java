package issues.issue11;

import org.apache.commons.io.IOUtils;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsCleaner;
import org.hibernate.criterion.MatchMode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class Issue11IntegrationTest extends IntegrationTest {

    @Autowired ItemDao itemDao;

    public String getFile(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName));
    }


   @Test
    // Найти все пункты с символами: *, †, ‡, § их не должно быть
   public void test(){
       List<Item> dirtyItems = itemDao.getLike("content", "**", MatchMode.ANYWHERE);
       dirtyItems.addAll(itemDao.getLike("content", "†", MatchMode.ANYWHERE));
       dirtyItems.addAll(itemDao.getLike("content", "‡", MatchMode.ANYWHERE));
       dirtyItems.addAll(itemDao.getLike("content", "§", MatchMode.ANYWHERE));

       //  то есть не должно быть не одного пункта с этими символами
       assertEquals(0, dirtyItems.size());
   }

    @Test
    /**
     * В некоторых пунктах * используется как умножение, по этому, если в тексте встречаеться одна звёздочка,
     * то её нужно оставить. Те * которые являются сносками, прейдётся вычислять вручную.
     */
    public void keepSingleStarTest() {
        // проверяю несколько случайных пунктов со звёздочкой в качестве умножения, то есть эт оне все пункты
        for (String itemNumber : asList("3.0436", "10.11151")) {
            assertTrue(itemDao.getByNumber(itemNumber).getContent().contains("*"));
        }

    }

    @Test
    /**
     * Заметил, что в случае когда перед звёздочкой стоит пробел то это сноска
     */
    public void testStarAsFootnote() {
        List<Item> dirtyItems = itemDao.getLike("content", " *", MatchMode.ANYWHERE);
        assertEquals(0, dirtyItems.size());
    }

    // 2. Метод для очистки базы данных
//    @Test
    public void cleanDB() throws IOException{
        // Этот медод запускается единажды для выполнения очистки всех пунктов
        // Чтобы запустить раскоментируй @Test и запусти только этот метод
        List<Item> items = itemDao.getAll();
        for(Item item : items) {
            String clean = ItemsCleaner.clean(item.getContent());
            if (!clean.equals(item.getContent())) {
                item.setContent(clean);
                itemDao.save(item);
            }
        }
    }
}
