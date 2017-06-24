package org.ayfaar.app.controllers.search.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.TermServiceImpl;
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
import java.util.Optional;

import static org.ayfaar.app.utils.TermService.TermProvider;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class DBCacheUnitTest {
    @Mock ObjectMapper objectMapper;
    @Mock CommonDao commonDao;
    @Mock CategoryDao categoryDao;
    @Mock
    TermService termService;
    @Mock TermDao termDao;
    @Mock LinkDao linkDao;
//    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks
    @Spy
    DBCache dbCache;
    @InjectMocks
    @Spy
    TermServiceImpl aliasesMap;

    /**
     * проверяем что-бы не сохранялось в базу если это не термин
     */
    @Test
    public void testPutNotTermStore() throws IOException {
        String termName = "Вектор";
        SearchCacheKey key = new SearchCacheKey(termName, "", 0);
        SearchResultPage page = new SearchResultPage();

        dbCache.put(key, page);
        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(termService, times(1)).get(termName);
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
        SearchCacheKey key = new SearchCacheKey(term.getName(), "", 1);
        SearchResultPage page = new SearchResultPage();

        TermProvider provider = aliasesMap.new TermProviderImpl(UriGenerator.generate(Term.class, term.getName()),
                UriGenerator.generate(Term.class, mainTerm.getName()), false);
        TermProvider mainProvider = aliasesMap.new TermProviderImpl(
                UriGenerator.generate(Term.class, mainTerm.getName()), null, false);

        when(termService.get(term.getName())).thenReturn(Optional.of(provider));

        aliasesMap.load();
        dbCache.put(key, page);


        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(termService, times(1)).get(term.getName());
        assertNotNull(termService.get(term.getName()));
        verify(commonDao, times(1)).save(anyObject());
    }


    /**
     * проверяем сохраняются ли в базу результаты поиска для термина
     */
    @Test
    public void testPutSearchResultPageWhenMainTermIsNull() throws IOException {
        Term term = new Term("Амплификационные Поток");

        SearchCacheKey key = new SearchCacheKey(term.getName(), "", 0);
        SearchResultPage page = new SearchResultPage();

        String uri = UriGenerator.generate(Term.class, "Амплификационные Поток");
        TermProvider provider = aliasesMap.new TermProviderImpl(uri, null, false);

        when(provider.getTerm()).thenReturn(term);
        when(termService.get(term.getName())).thenReturn(Optional.of(provider));


        dbCache.put(key, page);
        verify(objectMapper, times(1)).writeValueAsString(page);
        verify(termService, times(1)).get(term.getName());
        verify(commonDao, times(1)).save(anyObject());
    }



    /**
     * проверяем чтобы кеш извлекался из памяти, а не из базы
     */
    @Test
    public void testGettingSearchResultPageFromCacheInMemory() throws IOException {
        Term term = new Term("Миру");
        SearchCacheKey key = new SearchCacheKey(term.getName(), "", 1);
        SearchResultPage page = new SearchResultPage();

        when(termService.getTerm(term.getName())).thenReturn(term);

        dbCache.put(key, page);
        Cache.ValueWrapper value = dbCache.get(key);

        verify(commonDao, never()).get(CacheEntity.class, "uri", term.getUri());
        assertNotNull(value);
    }

    /**
     * проверяем чтобы кеш для результатов поиска извлекался из базы
     */
    @Test
    public void testGettingSearchResultPageFromCacheInDatabase() {
        Term term = new Term("АСТТМАЙ-РАА-А");
        term.setUri(UriGenerator.generate(Term.class, term.getName()));
        SearchCacheKey key = new SearchCacheKey(term.getName(), "", 0);
        String uri = UriGenerator.generate(Term.class, term.getName());
        TermProvider provider = aliasesMap.new TermProviderImpl(uri, null, false);


        when(termService.get(term.getName())).thenReturn(Optional.of(provider));
        when(provider.getTerm()).thenReturn(term);

        dbCache.get(key);

        verify(commonDao, times(1)).get(CacheEntity.class, term.getUri());
    }

    /**
     * проверяем чтобы кеш для содержания извлекался из базы
     */
    @Test
    public void testGettingCategoryPresentationFromCacheInDatabase() {
        Category category = new Category("БДК / Раздел III");
        category.setUri(UriGenerator.generate(Category.class, "БДК / Раздел III"));
        ContentsCacheKey key = new ContentsCacheKey(category.getName());

        when(categoryDao.get("uri", UriGenerator.generate(Category.class, category.getName()))).thenReturn(category);
        dbCache.get(key);

        verify(commonDao, times(1)).get(CacheEntity.class, "uri", category.getUri());
    }

    @Test
    public void testInvocationEventPublisherOnlyForNotTerm() {
        Term term = new Term("АСТТМАЙ-РАА-А");
        term.setUri(UriGenerator.generate(Term.class, term.getName()));
        SearchCacheKey key = new SearchCacheKey(term.getName(), "", 0);

        when(termService.get(term.getName())).thenReturn(null);

        dbCache.get(key);

//        verify(eventPublisher, times(1)).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void testNotInvocationEventPublisherForTerm() {
        Term term = new Term("АСТТМАЙ-РАА-А");
        term.setUri(UriGenerator.generate(Term.class, term.getName()));
        SearchCacheKey key = new SearchCacheKey(term.getName(), "", 0);
        String uri = UriGenerator.generate(Term.class, term.getName());
        TermProvider provider = aliasesMap.new TermProviderImpl(uri, null, false);

        when(termService.get(term.getName())).thenReturn(Optional.of(provider));
        when(provider.getTerm()).thenReturn(term);

        dbCache.get(key);

//        verify(eventPublisher, never()).publishEvent(any(ApplicationEvent.class));

    }
}
