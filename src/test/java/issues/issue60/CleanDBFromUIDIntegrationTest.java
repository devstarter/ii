package issues.issue60;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.*;
import org.ayfaar.app.model.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import static org.junit.Assert.*;

public class CleanDBFromUIDIntegrationTest extends IntegrationTest{
    @Autowired private UIDDao uidDao;
    @Autowired private ArticleDao articleDao;
    @Autowired private CategoryDao categoryDao;
    @Autowired private ItemDao itemDao;
    @Autowired private TermDao termDao;


    @Test
    public void test() {
        List<String> uidUris = uidDao.getAll();
        List<Article> articles = articleDao.getAll();
        List<Category> categories = categoryDao.getAll();
        List<Item> items = itemDao.getAll();
        List<Term> terms = termDao.getAll();

        int expectedNumberUris = articles.size() + categories.size() + items.size() + terms.size();

        assertEquals(expectedNumberUris, uidUris.size());
    }
}
