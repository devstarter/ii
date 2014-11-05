package issues.issue66;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;


public class TermControllerIntegrationTest extends IntegrationTest {
    @Autowired private TermController termController;
    @Autowired private TermDao termDao;
    @Autowired private LinkDao linkDao;
    @Autowired private AliasesMap aliasesMap;

    @Test
    @Transactional
    public void testRenameTerm1() {
        String oldName = "ВСЕ-Воля-ВСЕ-Разума";
        String newName = "test new name";
        termController.renameTerm(oldName, newName);

        Term term = aliasesMap.getTerm(newName);
        List<Link> links = linkDao.getAllLinks(term.getUri());

        assertEquals(UriGenerator.generate(Term.class, "Разум"), links.get(0).getUid1().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Test new name"), links.get(0).getUid2().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Test new name"), links.get(1).getUid1().getUri());
        assertEquals(UriGenerator.generate(Item.class, "2.0170"), links.get(1).getUid2().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Test new name"), links.get(2).getUid1().getUri());
        assertEquals(UriGenerator.generate(Term.class, "КРА-АГГА-АГГА"), links.get(2).getUid2().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Test new name"), links.get(3).getUid1().getUri());
        assertEquals(UriGenerator.generate(Item.class, "2.0148"), links.get(3).getUid2().getUri());
        assertNull(termDao.getByName("ВСЕ-Воля-ВСЕ-Разума"));
        assertEquals("Test new name", termDao.getByName("test new name").getName());
    }

    @Test
    @Transactional
    public void testRenameTerm2() {
        String oldName = "Временная Сущность";
        String newName = "Новая Сущность";
        termController.renameTerm(oldName, newName);

        Term term = aliasesMap.getTerm(newName);
        List<Link> links = linkDao.getAllLinks(term.getUri());

        assertEquals(UriGenerator.generate(Term.class, "Время"), links.get(0).getUid1().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Новая Сущность"), links.get(0).getUid2().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Вселенская Временная Сущность"), links.get(1).getUid1().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Новая Сущность"), links.get(1).getUid2().getUri());
        assertEquals(UriGenerator.generate(Term.class, "Новая Сущность"), links.get(2).getUid1().getUri());
        assertEquals(UriGenerator.generate(Item.class, "1.1036"), links.get(2).getUid2().getUri());
        assertNull(termDao.getByName("Временная Сущность"));
        assertEquals("Новая Сущность", termDao.getByName("Новая Сущность").getName());
    }
}
