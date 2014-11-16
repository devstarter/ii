package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.controllers.search.cache.DBCache;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
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
        controller.cache = mock(DBCache.class);
    }

    @Test
    public void testSearch() throws Exception {
        SearchResultPage page = (SearchResultPage) controller.search("Универсальная мерность", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();

        assertEquals(2, quotes.size());
        assertFalse(page.isHasMore());
        assertEquals("2.0140", quotes.get(0).getNumber());
        assertEquals("2.0220", quotes.get(1).getNumber());
    }

    @Test
    public void ННААССММ() throws Exception {
        SearchResultPage page = (SearchResultPage) controller.search("ННААССММ", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertEquals(20, quotes.size());
        assertTrue(page.isHasMore());
    }

    @Test
    public void test_cache() throws Exception {
        CustomObjectMapper objectMapper = new CustomObjectMapper();
        SearchResultPage page = (SearchResultPage) controller.search("ВЛОООМООТ", 0, null);
        String json = (String) controller.search("ВЛОООМООТ", 0, null);
        assertNotNull(page);
        assertNotNull(json);
        assertEquals(json, objectMapper.writeValueAsString(page));
    }

    @Test
    public void test_328() throws Exception {
        SearchResultPage page = (SearchResultPage) controller.search("328", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertTrue(quotes.size() > 0);
    }

    @Test
    public void ptic() {
        SearchResultPage page = (SearchResultPage) controller.search("птиц", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertTrue(quotes.size() > 0);
        assertNotEquals(quotes.get(0).getQuote(), "<strong>");
    }
}