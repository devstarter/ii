package org.ayfaar.app.controllers.search.cache;


import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.events.SimplePushEvent;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.TermsMarker;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CacheUpdaterUnitTest {
    @Mock CommonDao commonDao;
    @Mock
    TermService termService;
    @Mock ItemDao itemDao;
    @Mock TermsMarker termsMarker;
    @Mock NewSearchController searchController;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock CustomObjectMapper objectMapper;

    @InjectMocks
    @Spy
    CacheUpdater cacheUpdater;

    @Test
    public void testUpdate() throws IOException {
        List<String> fakeCache = Arrays.asList(UriGenerator.generate(Term.class, "Амплификационный Вектор"),
        UriGenerator.generate(Term.class, "Время"), UriGenerator.generate(Term.class, "АСТТМАЙ-РАА-А"));

        String uri = UriGenerator.generate(Term.class, "");

        when(commonDao.getLike(CacheEntity.class, "uri", (uri + "%"), Integer.MAX_VALUE))
                .thenReturn(CollectionUtils.transform(fakeCache, new Transformer() {
        @Override
        public Object transform(Object value) {
            Term term = new Term((String) value);
            term.setUri(UriGenerator.generate(Term.class, (String) value));
            return new CacheEntity(term, null);
        }
        }));

        when(objectMapper.writeValueAsString(anyObject())).thenReturn("fakeSearchResult");

        cacheUpdater.update();

        verify(termService, times(1)).reload();
        verify(commonDao, times(1)).getLike(CacheEntity.class, "uri", (uri + "%"), Integer.MAX_VALUE);
        verify(searchController, times(3)).searchWithoutCache(getValueFromUri(Term.class, anyString()), eq(0), anyString());
        verify(eventPublisher, times(1)).publishEvent(any(SimplePushEvent.class));
        verify(commonDao, times(3)).save(anyObject());
    }
}
