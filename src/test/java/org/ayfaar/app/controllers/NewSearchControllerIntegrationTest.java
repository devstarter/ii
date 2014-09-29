package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.junit.Before;
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

        assertEquals(2, quotes.size());
        assertFalse(page.isHasMore());
        assertEquals("2.0140", quotes.get(0).getNumber());
        assertEquals("2.0220", quotes.get(1).getNumber());
    }

    @Test
    public void test_ННААССММ() throws Exception {
        SearchResultPage page = controller.search("ННААССММ", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertEquals(20, quotes.size());
        assertFalse(page.isHasMore());
    }

    @Test
    public void test_328() throws Exception {
        SearchResultPage page = controller.search("328", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertTrue(quotes.size() > 0);
    }

    @Test
    public void птиц() {
        SearchResultPage page = controller.search("птиц", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertTrue(quotes.size() > 0);
        assertNotEquals(quotes.get(0).getQuote(), "<strong>");
    }
}