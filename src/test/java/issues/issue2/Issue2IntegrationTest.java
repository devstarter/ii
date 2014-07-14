package issues.issue2;

import org.ayfaar.app.IntegrationTest;
<<<<<<< HEAD
import org.ayfaar.app.dao.ItemDao;
=======
import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.importing.SpringConfiguration;
>>>>>>> 7f23c6e7e979eb5673a20ed474e7add63584dbbe
import org.ayfaar.app.model.Item;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
<<<<<<< HEAD
=======
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
>>>>>>> 7f23c6e7e979eb5673a20ed474e7add63584dbbe

import static org.junit.Assert.assertEquals;

public class Issue2IntegrationTest extends IntegrationTest {

    @Value("#{T(org.apache.commons.io.FileUtils).readFileToString(" +
            "T(org.springframework.util.ResourceUtils).getFile('classpath:issues/issue2/clean-item-3.0089.txt')" +
            ")}")
    String itemExpectedContent;

<<<<<<< HEAD
    @Autowired ItemDao itemDao;
=======
    @Autowired
    ItemDao itemDao;


    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
>>>>>>> 7f23c6e7e979eb5673a20ed474e7add63584dbbe

    @Test
    public void checkParticularItem() {
        Item item = itemDao.getByNumber("3.0089");
        assertEquals(itemExpectedContent, item.getContent());
    }

    // Ещё нужен тест на то, что больше ни в одном item нет слова Глава
}
