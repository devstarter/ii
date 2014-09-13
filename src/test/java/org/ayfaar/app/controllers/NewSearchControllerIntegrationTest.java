package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class NewSearchControllerIntegrationTest extends IntegrationTest {

    @Inject NewSearchController controller;

    @Before
    public void setup() {
        controller.cache = mock(SearchCache.class);
    }

    @Test
    public void testSearch() throws Exception {
        SearchResultPage page = controller.search("Универсальная мерность", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        for(Quote q : quotes) {
            System.out.println(q.getUri());
        }
        assertEquals(2, quotes.size());
        assertFalse(page.isHasMore());
        assertEquals("ии:пункт:2.0140", quotes.get(0).getUri());
        assertEquals("ии:пункт:2.0220", quotes.get(1).getUri());
    }

    @Test
    public void test_ННААССММ() throws Exception {
        SearchResultPage page = controller.search("ННААССММ", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertTrue(quotes.size() > 0);
    }
}