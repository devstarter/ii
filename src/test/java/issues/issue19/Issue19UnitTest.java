package issues.issue19;

import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.SearchController2;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.services.SearchService;
import org.ayfaar.app.utils.AliasesMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static net.sf.cglib.core.CollectionUtils.transform;
import static org.junit.Assert.assertEquals;

/**
 * о анотациях http://stackoverflow.com/q/20270391/975169
 */
@RunWith(MockitoJUnitRunner.class)
public class Issue19UnitTest {
    @Mock AliasesMap aliasesMap;

    @InjectMocks @Spy
    SearchService searchService = new SearchService();

    @InjectMocks
    SearchController2 controller = new SearchController2();

    @Test
    public void testSequence() {
        String q = "a";
        List<String> fakeTerms = asList("a", "1 a", "bterfd", "b-aaaa", "aa", "242a424");
        //noinspection unchecked
        Mockito.when(aliasesMap.getAllTerms()).thenReturn(transform(fakeTerms, new Transformer() {
            @Override
            public Object transform(Object value) {
                return new Term((String) value);
            }
        }));

        List<String> suggestions = controller.suggestions(q);

        assertEquals(5, suggestions.size());
        assertEquals("a", suggestions.get(0));
        assertEquals("aa", suggestions.get(1));
        assertEquals("1 a", suggestions.get(2));
        assertEquals("b-aaaa", suggestions.get(3));
        assertEquals("242a424", suggestions.get(4));
    }

    // todo: проверить на максимальное количество
}
