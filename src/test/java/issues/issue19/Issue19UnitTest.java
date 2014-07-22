package issues.issue19;

import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.SearchController2;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.services.SearchService;
import org.ayfaar.app.utils.AliasesMap;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static java.util.Arrays.asList;
import static net.sf.cglib.core.CollectionUtils.transform;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class Issue19UnitTest {
    @Test
    public void testSequence() {
        String q = "a";
        List<String> fakeTerms = asList("a", "1 a", "bterfd", "b-aaaa", "aa", "242a424");
        AliasesMap fakeAliasesMap = Mockito.mock(AliasesMap.class);
        //noinspection unchecked
        Mockito.when(fakeAliasesMap.getAllTerms()).thenReturn(transform(fakeTerms, new Transformer() {
            @Override
            public Object transform(Object value) {
                return new Term((String) value);
            }
        }));
        SearchService searchService = new SearchService();
        setField(searchService, "aliasesMap", fakeAliasesMap);

        SearchController2 controller = new SearchController2();
        setField(controller, "searchService", searchService);

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
