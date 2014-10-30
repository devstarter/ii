package org.ayfaar.app.controllers.search.cache;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.CategoryController;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.ayfaar.app.utils.contents.ContentsHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.Assert.*;

public class DBCacheIntegrationTest extends IntegrationTest {
    @Autowired
    private DBCache dbCache;
    @Autowired
    private NewSearchController searchController;
    @Autowired
    private ContentsHelper contentsHelper;

    @Test
    @Ignore
    public void testPutSearchResultPage() {
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey("Время", 2);
        SearchResultPage page = (SearchResultPage)searchController.search("Время", 0, null);
        /*CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey("АА-ФТОО-СС-СТ", 1);
        SearchResultPage page = (SearchResultPage)controller.search("АА-ФТОО-СС-СТ", 0, null);*/
        /*CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey("ААИИГЛА-МАА", 1);
        SearchResultPage page = (SearchResultPage)controller.search("ААИИГЛА-МАА", 0, null);*/

        dbCache.put(key, page);
    }

    @Test
    @Ignore
    public void testPutCategoryPresentation() {
        /*CategoryPresentation contents = contentsHelper.createContents("БДК / Раздел III");
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey("БДК / Раздел III", 0);*/
        CategoryPresentation contents = contentsHelper.createContents("БДК / Раздел III / Глава 2");
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey("БДК / Раздел III / Глава 2", 0);

        dbCache.put(key, contents);
    }

    @Test
    public void testGetSearchResultPage() {
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey("Время", 2);
        SearchResultPage expectedPage = (SearchResultPage)searchController.search("Время", 0, null);
        SearchResultPage fromCache = (SearchResultPage)dbCache.get(key).get();

        assertEquals(expectedPage, fromCache);
    }

    @Test
    public void testGetCategoryPresentation() {
        CategoryPresentation expectedContents = contentsHelper.createContents("БДК / Раздел III");
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey("БДК / Раздел III", 0);
        CategoryPresentation fromCache = (CategoryPresentation)dbCache.get(key).get();

        assertEquals(expectedContents, fromCache);
    }
}
