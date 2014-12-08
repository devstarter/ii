package org.ayfaar.app.controllers.search.cache;


import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CacheUpdaterUnitTest {
    @Mock CommonDao commonDao;
    @Mock TermsMap termsMap;
    @Mock NewSearchController searchController;

    @InjectMocks
    @Spy
    CacheUpdater cacheUpdater;

    @Test
    public void testUpdate() {
        List<String> fakeCache = Arrays.asList(UriGenerator.generate(Term.class, "Амплификационный Вектор"),
                UriGenerator.generate(Term.class, "Время"), UriGenerator.generate(Term.class, "АСТТМАЙ-РАА-А"));

        when(commonDao.getAll(CacheEntity.class)).thenReturn(CollectionUtils.transform(fakeCache, new Transformer() {
            @Override
            public Object transform(Object value) {
                Term term = new Term((String) value);
                term.setUri(UriGenerator.generate(Term.class, (String) value));
                return new CacheEntity(term, null);
            }
        }));

        cacheUpdater.update();

        verify(termsMap, times(1)).reload();
        verify(commonDao, times(1)).getAll(CacheEntity.class);
        verify(searchController, times(3)).search(getValueFromUri(Term.class, anyString()), eq(0), anyString());
    }
}
