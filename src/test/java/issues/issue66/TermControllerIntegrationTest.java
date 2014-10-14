package issues.issue66;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;


public class TermControllerIntegrationTest extends IntegrationTest {
    @Autowired private TermController termController;
    @Autowired private TermDao termDao;
    @Autowired private LinkDao linkDao;

    @Test
    public void testRenameTerm1() {
        String oldName = "ВСЕ-Воля-ВСЕ-Разума";
        String newName = "test new name";
        termController.renameTerm(oldName, newName);

        Term term = termDao.getByName(newName);
        List<Link> links = linkDao.getAllLinks(term.getUri());

        assertEquals("ии:термин:Разум", links.get(0).getUid1().getUri());
        assertEquals("ии:термин:test new name", links.get(0).getUid2().getUri());
        assertEquals("ии:термин:test new name", links.get(1).getUid1().getUri());
        assertEquals("ии:пункт:2.0170", links.get(1).getUid2().getUri());
        assertEquals("ии:термин:test new name", links.get(2).getUid1().getUri());
        assertEquals("ии:термин:КРА-АГГА-АГГА", links.get(2).getUid2().getUri());
        assertEquals("ии:термин:test new name", links.get(3).getUid1().getUri());
        assertEquals("ии:пункт:2.0148", links.get(3).getUid2().getUri());
    }

    @Test
    public void testRenameTerm2() {
        String oldName = "Временная Сущность";
        String newName = "Новая Сущность";
        termController.renameTerm(oldName, newName);

        Term term = termDao.getByName(newName);
        List<Link> links = linkDao.getAllLinks(term.getUri());

        assertEquals("ии:термин:Время", links.get(0).getUid1().getUri());
        assertEquals("ии:термин:Новая Сущность", links.get(0).getUid2().getUri());
        assertEquals("ии:термин:Вселенская Временная Сущность", links.get(1).getUid1().getUri());
        assertEquals("ии:термин:Новая Сущность", links.get(1).getUid2().getUri());
        assertEquals("ии:термин:Новая Сущность", links.get(2).getUid1().getUri());
        assertEquals("ии:пункт:1.1036", links.get(2).getUid2().getUri());
    }

    /**
     * тест запускать после выполнения testRenameTerm1 и testRenameTerm2
     */
    @Test
    public void testIfOldTermRemovedAndNewTermAdded() {
        assertNull(termDao.getByName("ВСЕ-Воля-ВСЕ-Разума"));
        assertEquals("Test new name", termDao.getByName("test new name").getName());
        assertNull(termDao.getByName("Временная Сущность"));
        assertEquals("Новая Сущность", termDao.getByName("Новая Сущность").getName());
    }
}
