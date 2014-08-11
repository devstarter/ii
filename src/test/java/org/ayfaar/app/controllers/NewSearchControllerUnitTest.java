package org.ayfaar.app.controllers;

import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static net.sf.cglib.core.CollectionUtils.transform;
import static org.ayfaar.app.controllers.NewSearchController.PAGE_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewSearchControllerUnitTest {

    @Mock SearchDao searchDao;
    @Mock SearchCache cache;
    @Mock SearchQuotesHelper handleItems;
    @Mock AliasesMap aliasesMap;
    @InjectMocks @Spy
    NewSearchController controller;

    @Before
    public void setUp() {
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHasMore() {
        String q = "время";
        Term term = new Term(q);
        when(aliasesMap.getTerm(q)).thenReturn(term);

        List<String> morphs = asList(q);
        when(controller.getAllMorphs(anyList())).thenReturn(morphs);

        List items = mock(List.class);
        when(searchDao.findInItems(morphs, 0, PAGE_SIZE+1, null)).thenReturn(items);
        when(items.size()).thenReturn(21);

        SearchResultPage page = controller.search(q, 0, null);
        assertTrue(page.isHasMore());
        verify(searchDao, only()).findInItems(anyList(), anyInt(), anyInt(), anyString());
        verify(items).remove(20);
    }

    // в таком же стиле можно добавить тесты на:
    // поиск фразы - не термина
    // поиск второй страницы
    // поиск по двумя запросами в базу (второй для синонимов)

    @Test
    public void testSearchPhrase() {
        String phrase = "каждый момент";

        controller.search(phrase, 0, null);
        verify(searchDao, only()).findInItems(asList(phrase), 0, PAGE_SIZE+1, null);
    }

    @Test
    public void testSearchSynonyms() {
        String query = "Ирркогликтивная Квалитация";
        Term term = new Term(query);
        when(aliasesMap.getTerm(query)).thenReturn(term);

        List<String> morphs = asList(query);
        when(controller.getAllMorphs(anyList())).thenReturn(morphs);

        controller.search(query, 0, null);

        //List<Item> actual = searchDao.findInItems(asList(query), 0, PAGE_SIZE+1, null);
        //actual.addAll(searchDao.findInItems(morphs, 0, PAGE_SIZE - actual.size()+1, null));

        verify(searchDao, times(2)).findInItems(anyList(), anyInt(), anyInt(), anyString());
    }
}
