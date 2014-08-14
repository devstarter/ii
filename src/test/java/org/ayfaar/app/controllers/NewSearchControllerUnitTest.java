package org.ayfaar.app.controllers;

import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static net.sf.cglib.core.CollectionUtils.transform;
import static org.ayfaar.app.controllers.NewSearchController.PAGE_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewSearchControllerUnitTest {

    @Mock SearchDao searchDao;
    @Mock SearchCache cache;
    @Mock SearchQuotesHelper handleItems;
    @Mock AliasesMap aliasesMap;
    @InjectMocks @Spy
    NewSearchController controller;

    @Before
    public void setUp() {
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHasMore() {
        String q = "время";
        Term term = new Term(q);
        when(aliasesMap.getTerm(q)).thenReturn(term);

        List<String> morphs = asList(q);
        when(controller.getAllMorphs(anyList())).thenReturn(morphs);

        List items = mock(List.class);
        when(searchDao.findInItems(morphs, 0, PAGE_SIZE+1, null)).thenReturn(items);
        when(items.size()).thenReturn(21);

        SearchResultPage page = controller.search(q, 0, null);
        assertTrue(page.isHasMore());
        verify(searchDao, only()).findInItems(anyList(), anyInt(), anyInt(), anyString());
        verify(items).remove(20);
    }

    // в таком же стиле можно добавить тесты на:
    // поиск фразы - не термина
    // поиск второй страницы
    // поиск по двумя запросами в базу (второй для синонимов)

    @Test
    public void testSearchPhrase() {
        /*
        Суть теста состоит в том, что-бы проверить коррекстность вызовов всех методов внутри controller.search
        (мы ведь здесь тестируем NewSearchController, а не SearchDao - это же мок) для поисковой фразы не термина.

          В этом тесте ты проверяешь просто корректность работы мокито :). То есть ты добавил условие что именно должен
          вернуть метод и вызвал этот метод. Сам код ты вообще не проверил :)
         */
        String phrase = "каждый момент";
        List<String> items = asList("1.0032","1.0122", "1.0159", "1.248", "1.0311", "1.0698", "1.0819", "1.0912", "1.0914", "2.0520",
                "2.0943", "3.0104", "3.0275", "3.0392", "3.0514", "3.0573", "10.10021", "10.10105", "10.10176", "10.10177");

        when(searchDao.findInItems(asList(phrase), 0, PAGE_SIZE+1, null)).thenReturn(transform(items, new Transformer() {
            @Override
            public Object transform(Object o) {
                return new Item((String) o);
            }
        }));

        List<Item> actual = searchDao.findInItems(asList(phrase), 0, PAGE_SIZE+1, null);

        assertTrue(actual.size() == 20);
        assertEquals("1.0032", actual.get(0).getNumber());
        assertEquals("2.0943", actual.get(10).getNumber());
        assertEquals("10.10177", actual.get(19).getNumber());

        // как бы я делал:
        controller.search(phrase, 0, null);
        verify(searchDao, only()).findInItems(asList(phrase), 0, PAGE_SIZE+1, null);
    }

    @Test
    public void testSearchSecondPage() {
        /*
        Та же ситуация, тут нужно просто проверить что searchDao.findInItems вызвался дважды и во второй раз для
        получения недостающего количества результатов.
           В этом случае должен быть запрос на синонимы и получение падежей для них, если есть синонимы.
           Этот тест можно совместить со следующим
         */
        String query = "Фокусной Динамики";
        int pageNumber = 1;
        List<String> items = asList("1.0149","1.0151", "1.0155", "1.161", "1.0173", "1.0181", "1.0183", "1.0185", "1.0187", "1.0196",
                "1.0199", "1.0209", "1.0253", "1.0262", "1.0265", "1.0274", "1.0285", "1.0286", "1.0287", "1.0288");

        when(searchDao.findInItems(asList(query), pageNumber, PAGE_SIZE+1, null)).thenReturn(transform(items, new Transformer() {
            @Override
            public Object transform(Object o) {
                return new Item((String) o);
            }
        }));

        List<Item> actual = searchDao.findInItems(asList(query), pageNumber, PAGE_SIZE+1, null);
        assertTrue(actual.size() == 20);
        assertEquals("1.0149", actual.get(0).getNumber());
        assertEquals("1.0199", actual.get(10).getNumber());
        assertEquals("1.0288", actual.get(19).getNumber());
    }

    @Test
    public void testSearchSynonyms() {
        String query = "Ирркогликтивная Квалитация";
        Term term = new Term(query);
        when(aliasesMap.getTerm(query)).thenReturn(term);

        List<String> morphs = asList(query);
        when(controller.getAllMorphs(anyList())).thenReturn(morphs);

        List<String> expected = asList("2.0200","2.0201", "2.0424", "2.0425", "2.0431", "2.0433", "2.0434", "2.0435", "2.0440", "2.0441");

        when(searchDao.findInItems(asList(query), 0, PAGE_SIZE, null)).thenReturn(transform(expected, new Transformer() {
            @Override
            public Object transform(Object o) {
                return new Item((String)o);
            }
        }));


        List<Item> actual = searchDao.findInItems(asList(query), 0, PAGE_SIZE, null);
        actual.addAll(searchDao.findInItems(morphs, 0, PAGE_SIZE - actual.size() + 1, null));

        assertTrue(actual.size() == 10);
        assertEquals("2.0200", actual.get(0).getNumber());
        assertEquals("2.0433", actual.get(5).getNumber());
        assertEquals("2.0441", actual.get(9).getNumber());
    }
}
