package issues.issue11;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsCleaner;
import org.hibernate.criterion.MatchMode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class Issue11IntegrationTest extends IntegrationTest {

    @Autowired ItemDao itemDao;

    public String getFile(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName));
    }


   @Test
    // 1. Найти все пункты с символами: *, †, ‡, § их не должно быть
   public void test(){
       List<Item> dirtyItems = itemDao.getLike("content", "*", MatchMode.ANYWHERE);
       dirtyItems.addAll(itemDao.getLike("content", "†", MatchMode.ANYWHERE));
       dirtyItems.addAll(itemDao.getLike("content", "‡", MatchMode.ANYWHERE));
       dirtyItems.addAll(itemDao.getLike("content", "§", MatchMode.ANYWHERE));

       //  то есть не должно быть не одного пункта с этими символами
       assertEquals(0, dirtyItems.size());
   }

    // пренести этот метод в ItemsCleaner.clean
    public String cleanStr(String str) {

        List<String> notContain = new ArrayList<String>();
        notContain.add("*");
        notContain.add("†");
        notContain.add("‡");
        notContain.add("§");

        for (int i = 0; i < notContain.size(); i++) {
            str = StringUtils.replace(str, notContain.get(i), "");
        }

       return str;

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
