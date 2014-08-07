package org.ayfaar.app.controllers.search;

import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Transformer;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchDaoIntegrationTest extends IntegrationTest{
    @Inject
    private SearchDao searchDao;

    private final List<String> queries;
    private final int pageSize = NewSearchController.PAGE_SIZE;

    public SearchDaoIntegrationTest() {
        queries = unmodifiableList(asList("времён", "времена", "временам", "временами", "временах", "временем", "времени", "время"));
    }

    @Test(timeout = 10000)
    public void testTimeForMethodFindInItems() {
        int skip = 0;
        long start = System.currentTimeMillis();
        searchDao.findInItems(queries, skip, pageSize);
        long end = System.currentTimeMillis();
        System.out.println("searchDao.findInItems delta = " + (end - start));
    }

    @Test
    public void testEqualityForMethodFindInItemsForFirstCall() {
        int skip = 0;
        List<Item> actual = searchDao.findInItems(queries, skip, pageSize);

        assertEquals(20, actual.size());
        assertEquals("1.0003", actual.get(0).getNumber());
        assertEquals("1.0008", actual.get(1).getNumber());
        assertEquals("1.0028", actual.get(9).getNumber());
        assertEquals("1.0048", actual.get(18).getNumber());
        assertEquals("1.0050", actual.get(19).getNumber());
    }

    @Test
    public void testEqualityForMethodFindInItemsForTenthCall() {
        int skip = pageSize * 10;
        List<Item> actual = searchDao.findInItems(queries, skip, pageSize);

        assertEquals(20, actual.size());
        assertEquals("1.0552", actual.get(0).getNumber());
        assertEquals("1.0556", actual.get(1).getNumber());
        assertEquals("1.0576", actual.get(9).getNumber());
        assertEquals("1.0606", actual.get(18).getNumber());
        assertEquals("1.0608", actual.get(19).getNumber());
    }

    @Test
    public void testOneWorkSearch() {
        List<Item> items = searchDao.findInItems(asList("апокликмия"), 0, pageSize);
        assertEquals(2, items.size());
        assertEquals("12.14021", items.get(0).getNumber());
        assertEquals("12.14023", items.get(1).getNumber());
    }

    /*@Test
    public void testFindInItemsWithFilter() {
        List<Item> actual = searchDao.findInItems(queries, 0, 4000, "3.1201");

        for(Item i : actual) {
            System.out.println(i.getNumber());
        }
    }*/

    @Test
    public void testFilter() {
        List<Item> actual = searchDao.testFilter(queries, 0, 4000, "3.1201");

        for(Item i : actual) {
            System.out.println(i.getNumber());
        }
    }

    @Test
    /*
    Тест на правильную последовательность пунктом, сначала должны быть пункты из самых ранних томов.
    SQL:
    SELECT  *
    FROM `ii`.`item`
    WHERE `content` LIKE '%ААИИГЛА-МАА%'
    ORDER BY cast(number as decimal), `uri` ASC
    LIMIT 20;
    это без учёта разных знаком не по краям фразы, но по идее должно быть тоже самое
     */
    public void testOrder() {
        final List<Item> items = searchDao.findInItems(asList("ААИИГЛА-МАА"), 0, pageSize);
        @SuppressWarnings("unchecked")
        List<String> numbers = CollectionUtils.transform(items, new Transformer() {
            @Override
            public Object transform(Object value) {
                return ((Item) value).getNumber();
            }
        });
        assertEquals(13, numbers.size());
        assertEquals(1, numbers.indexOf("1.1024"));
        assertTrue(numbers.indexOf("10.11809") > numbers.indexOf("3.0056"));
    }
}
