package org.ayfaar.app.controllers;

import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.*;
import org.ayfaar.app.utils.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.ayfaar.app.utils.TermsMap.TermProvider;
import static org.ayfaar.app.controllers.NewSearchController.PAGE_SIZE;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewSearchControllerUnitTest {

    @Mock SearchDao searchDao;
    @Mock CommonDao commonDao;
    @Mock TermMorphDao termMorphDao;
    @Mock SearchCache cache;
    @Mock SearchQuotesHelper handleItems;
    @Mock TermsMap termsMap;
    @Mock TermDao termDao;
    @Mock LinkDao linkDao;
    @InjectMocks @Spy
    NewSearchController controller;
    @InjectMocks @Spy
    TermsMapImpl aliasesMap;


    @Before
    public void setUp() {
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHasMore() {
        String q = "время";
        TermProvider provider = aliasesMap.new TermProviderImpl(q, null, false);

        when(termsMap.getTermProvider(q)).thenReturn(provider);

        List<String> morphs = asList(q);
        when(controller.getAllMorphs(anyList())).thenReturn(morphs);

        List items = mock(List.class);
        // чтобы сработал это запрос вместо реальных методов нужно проставлять Мокито мачеры
        doReturn(items).when(searchDao).findInItems(any(List.class), anyInt(), anyInt(), anyString());
        doReturn(21).when(items).size();

        aliasesMap.load(); // а зачем здесь лоад?
        SearchResultPage page = (SearchResultPage) controller.search(q, 0, null);

        assertTrue(page.isHasMore());
        verify(searchDao, only()).findInItems(anyList(), anyInt(), anyInt(), anyString());
        verify(items).remove(20);
    }

    @Test
    public void testSearchPhrase() {
        String phrase = "каждый момент";

        controller.search(phrase, 0, null);
        verify(searchDao, only()).findInItems(asList(phrase), 0, PAGE_SIZE + 1, null);
    }
}
