package org.ayfaar.app.controllers.search.cache;


import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.TermsMarker;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CacheUpdaterUnitTest {
    @Mock CommonDao commonDao;
    @Mock TermsMap termsMap;
    @Mock ItemDao itemDao;
    @Mock TermsMarker termsMarker;
    @Mock NewSearchController searchController;

    @InjectMocks
    @Spy
    CacheUpdater cacheUpdater;


    @Test
    public void testUpdateItemContent() {
        List<String> fakeItems = Arrays.asList("content1", "content2", "content3");
        List<String> markedItems = Arrays.asList("<term>content1</term>", "</term>content2</term>", "<term>content3</term>");

        when(itemDao.getAll()).thenReturn(CollectionUtils.transform(fakeItems, new Transformer() {
            @Override
            public Object transform(Object value) {
                Item item = new Item();
                item.setUri(UriGenerator.generate(Item.class, (String) value));
                item.setContent((String) value);
                return item;
            }
        }));

        when(termsMarker.mark(fakeItems.get(0))).thenReturn(markedItems.get(0));

        cacheUpdater.updateItemContent();

        verify(itemDao, times(1)).getAll();
        verify(termsMarker, times(3)).mark(anyString());
        verify(itemDao, times(3)).save(any(Item.class));
    }
}
