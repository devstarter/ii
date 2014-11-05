package issues.issue66;

import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TermControllerUnitTest {
    @InjectMocks @Spy TermController termController;
    @Mock TermDao termDao;
    @Mock AliasesMap aliasesMap;
    @Mock LinkDao linkDao;

    private List<Link> links;

    @Before
    public void init() {
        Term term1 = new Term("Разум");
        term1.setUri(UriGenerator.generate(term1));
        Term term2 = new Term("New name");
        term2.setUri(UriGenerator.generate(term2));
        Term term3 = new Term("КРА-АГГА-АГГА");
        term3.setUri(UriGenerator.generate(term3));
        Item item1= new Item("2.0170");
        item1.setUri(UriGenerator.generate(item1));
        Item item2 = new Item("2.0148");
        item2.setUri(UriGenerator.generate(item2));

        links = asList(new Link(term1, term2), new Link(term2, item1),
                new Link(term2, term3), new Link(term2, item2));
    }

    @Test
    public void testRenameTerm() {
        String oldName = "ВСЕ-Воля-ВСЕ-Разума";
        String newName = "New name";
        Term oldTerm = new Term(oldName);
        Term newTerm = new Term(newName);


        when(aliasesMap.getTerm(oldName)).thenReturn(oldTerm);
        when(termDao.getByName(newName)).thenReturn(newTerm);
        when(linkDao.getAllLinks(oldTerm.getUri())).thenReturn(links);
        doNothing().when(termController).remove(anyString());
        when(linkDao.save((Link)anyObject())).thenReturn(new Link());

        termController.renameTerm(oldName, newName);

        verify(termController, times(1)).add(anyString(), anyString(), anyString());
        verify(termController, times(1)).remove(oldName);
        verify(aliasesMap, times(1)).getTerm(oldName);
        verify(termDao, times(2)).getByName(anyString());
        verify(linkDao, times(1)).getAllLinks(anyString());
        verify(linkDao, times(1)).save(links.get(0));
    }
}
