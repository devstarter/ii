package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@Ignore
@Configuration
public class NewSearchControllerIntegrationTest extends IntegrationTest {

    @Inject NewSearchController controller;

    @Test
    public void testSearch() throws Exception {
        SearchResultPage page = controller.search("Универсальная мерность", 0, null);
        assertNotNull(page);
        List<Quote> quotes = page.getQuotes();
        assertEquals(2, quotes.size());
        assertFalse(page.isHasMore());
        assertEquals("ии:пункт:2.0140", quotes.get(0).getUri());
        assertEquals("ии:пункт:2.0220", quotes.get(1).getUri());
    }

    @Bean
    public SearchCache mockCache() {
        return Mockito.mock(SearchCache.class);
    }
}