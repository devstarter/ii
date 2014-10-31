package org.ayfaar.app.controllers.search.cache;


import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.Cache;

import java.io.IOException;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DBCacheUnitTest {
    @Mock CustomObjectMapper objectMapper;
    @Mock CommonDao commonDao;
    @Mock CategoryDao categoryDao;
    @Mock AliasesMap aliasesMap;
    @InjectMocks
    @Spy
    DBCache dbCache;

    /**
     * проверяем что-бы не сохранялось в базу если это не термин
     */
    @Test
    public void testPutNotTermStore() throws IOException {
        String termName = "Рис.";
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey(termName, 1);
        SearchResultPage page = new SearchResultPage();

        dbCache.put(key, page);
        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(aliasesMap, times(1)).getTerm(termName);
        verify(commonDao, never()).save(anyObject());
    }

    /**
     * проверяем сохраняется ли в базу json
     */
    @Test
    public void testPutSearchResultPage() throws IOException {
        String termName = "ААИИГЛА-МАА";
        Term term = new Term(termName);
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey(termName, 1);
        SearchResultPage page = new SearchResultPage();

        when(aliasesMap.getTerm(termName)).thenReturn(term);

        dbCache.put(key, page);
        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(aliasesMap, times(1)).getTerm(termName);
        verify(commonDao, times(1)).save(anyObject());
    }

    /**
     * проверяем чтобы кеш извлекался из памяти, а не из базы
     */
    @Test
    public void testGettingSearchResultPageFromCacheInMemory() throws IOException {
        String termName = "Миру";
        Term term = new Term(termName);
        CacheKeyGenerator.SearchCacheKey key = new CacheKeyGenerator.SearchCacheKey(termName, 1);
        SearchResultPage page = new SearchResultPage();

        when(aliasesMap.getTerm(termName)).thenReturn(term);

        dbCache.put(key, page);
        Cache.ValueWrapper value = dbCache.get(key);

        verify(commonDao, never()).get(JsonEntity.class, "uid", term);
        assertNotNull(value);
    }

    /**
     * проверяем чтобы кеш извлекался из базы
     */
    @Test
    public void testGettingCategoryPresentationFromCacheInDatabase() throws IOException {
        String categoryName = "БДК / Раздел III";
        Category category = new Category(categoryName);
        CacheKeyGenerator.ContentsCacheKey key = new CacheKeyGenerator.ContentsCacheKey(categoryName);

        when(categoryDao.get("uri", UriGenerator.generate(Category.class, categoryName))).thenReturn(category);
        dbCache.get(key);

        verify(commonDao, times(1)).get(JsonEntity.class, "uid", category);
    }
}
