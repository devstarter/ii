package org.ayfaar.app.controllers.search.cache;


import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.NewAliasesMap;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.Cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.ayfaar.app.utils.TermsMap.TermProvider;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DBCacheUnitTest {
    @Mock CustomObjectMapper objectMapper;
    @Mock CommonDao commonDao;
    @Mock CategoryDao categoryDao;
    @Mock TermsMap termsMap;
    @Mock TermDao termDao;
    @Mock LinkDao linkDao;
    @InjectMocks
    @Spy
    DBCache dbCache;
    @InjectMocks
    @Spy
    NewAliasesMap aliasesMap;

    /**
     * проверяем что-бы не сохранялось в базу если это не термин
     */
    @Test
    public void testPutNotTermStore() throws IOException {
        String termName = "Вектор";
        SearchCacheKey key = new SearchCacheKey(termName, 1);
        SearchResultPage page = new SearchResultPage();

        dbCache.put(key, page);
        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(termsMap, times(1)).getTermProvider(termName);
        verify(commonDao, never()).save(anyObject());
    }

    /**
     * проверяем сохраняется ли в базу результаты поиска для main термина
     */
    @Test
    @Ignore
    public void testPutSearchResultPageForMainTerm() throws IOException {
        Term term = new Term("Душа человека");
        Term mainTerm = new Term("АСТТМАЙ-РАА-А");
        SearchCacheKey key = new SearchCacheKey(term.getName(), 1);
        SearchResultPage page = new SearchResultPage();

        TermProvider provider = aliasesMap.new TermProviderImpl(UriGenerator.generate(Term.class, term.getName()),
                UriGenerator.generate(Term.class, mainTerm.getName()), false);
        TermProvider mainProvider = aliasesMap.new TermProviderImpl(
                UriGenerator.generate(Term.class, mainTerm.getName()), null, false);

        when(termsMap.getTermProvider(term.getName())).thenReturn(provider);

        aliasesMap.load();
        dbCache.put(key, page);


        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(termsMap, times(1)).getTermProvider(term.getName());
        assertNotNull(termsMap.getTermProvider(term.getName()));
        verify(commonDao, times(1)).save(anyObject());
    }


    /**
     * проверяем сохраняются ли в базу результаты поиска для термина
     */
    @Test
    public void testPutSearchResultPageWhenMainTermIsNull() throws IOException {
        Term term = new Term("Амплификационные Поток");

        SearchCacheKey key = new SearchCacheKey(term.getName(), 1);
        SearchResultPage page = new SearchResultPage();

        String uri = UriGenerator.generate(Term.class, "Амплификационные Поток");
        TermProvider provider = aliasesMap.new TermProviderImpl(uri, null, false);

        when(provider.getTerm()).thenReturn(term);
        when(termsMap.getTermProvider(term.getName())).thenReturn(provider);


        dbCache.put(key, page);
        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(termsMap, times(1)).getTermProvider(term.getName());
        verify(commonDao, times(1)).save(anyObject());
    }



    /**
     * проверяем чтобы кеш извлекался из памяти, а не из базы
     */
    @Test
    public void testGettingSearchResultPageFromCacheInMemory() throws IOException {
        Term term = new Term("Миру");
        SearchCacheKey key = new SearchCacheKey(term.getName(), 1);
        SearchResultPage page = new SearchResultPage();

        when(termsMap.getTerm(term.getName())).thenReturn(term);

        dbCache.put(key, page);
        Cache.ValueWrapper value = dbCache.get(key);

        verify(commonDao, never()).get(JsonEntity.class, "uri", term.getUri());
        assertNotNull(value);
    }

    /**
     * проверяем чтобы кеш для результатов поиска извлекался из базы
     */
    @Test
    public void testGettingSearchResultPageFromCacheInDatabase() {
        Term term = new Term("АСТТМАЙ-РАА-А");
        term.setUri(UriGenerator.generate(Term.class, term.getName()));
        SearchCacheKey key = new SearchCacheKey(term.getName(), 1);
        String uri = UriGenerator.generate(Term.class, term.getName());
        TermProvider provider = aliasesMap.new TermProviderImpl(uri, null, false);


        when(termsMap.getTermProvider(term.getName())).thenReturn(provider);
        when(provider.getTerm()).thenReturn(term);

        dbCache.get(key);

        verify(commonDao, times(1)).get(JsonEntity.class, term.getUri());
    }

    /**
     * проверяем чтобы кеш для содержания извлекался из базы извлекался из базы
     */
    @Test
    public void testGettingCategoryPresentationFromCacheInDatabase() {
        Category category = new Category("БДК / Раздел III");
        category.setUri(UriGenerator.generate(Category.class, "БДК / Раздел III"));
        ContentsCacheKey key = new ContentsCacheKey(category.getName());

        when(categoryDao.get("uri", UriGenerator.generate(Category.class, category.getName()))).thenReturn(category);
        dbCache.get(key);

        verify(commonDao, times(1)).get(JsonEntity.class, "uri", category.getUri());
    }
}
