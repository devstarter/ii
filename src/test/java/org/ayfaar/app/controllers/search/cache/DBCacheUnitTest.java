package org.ayfaar.app.controllers.search.cache;


import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.CommonDao;
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

import java.io.IOException;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DBCacheUnitTest {
    @Mock CustomObjectMapper objectMapper;
    @Mock CommonDao commonDao;
    @Mock AliasesMap aliasesMap;
    @InjectMocks
    @Spy
    DBCache dbCache;

    @Test
    public void testPutSearchResultToDB() throws IOException {
        JsonEntity entity = new JsonEntity();
        when(commonDao.save(entity)).thenReturn(entity);
        when(objectMapper.writeValueAsString(new SearchResultPage())).thenReturn(new String("result"));

        dbCache.put(new CacheKeyGenerator.SearchCacheKey("время", 2), new SearchResultPage());

        verify(commonDao, times(1)).save(anyObject());
        verify(objectMapper, times(1)).writeValueAsString(anyObject());
    }

    @Test
    public void testGetSearchResultFromDB() throws IOException {
        Term term = new Term("ГЛООГОЛМ");
        term.setUri(UriGenerator.generate(Term.class, "ГЛООГОЛМ"));
        JsonEntity entity = new JsonEntity();
        entity.setJsonContent("something json");
        entity.setUri(term);

        when(aliasesMap.getTerm("ГЛООГОЛМ")).thenReturn(term);
        when(commonDao.get(JsonEntity.class, "uri", term)).thenReturn(entity);

        dbCache.get(new CacheKeyGenerator.SearchCacheKey(term.getName(), 0));

        verify(commonDao, times(1)).get(JsonEntity.class, "uri", term);
        verify(objectMapper, times(1)).readValue(entity.getJsonContent(), SearchResultPage.class);
    }
}
