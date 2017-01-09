package org.ayfaar.app.controllers.search;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.CollectionUtils;
import org.ayfaar.app.utils.Transformer;
import org.junit.Ignore;
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
        searchDao.findInItems(queries, skip, pageSize, null);
        long end = System.currentTimeMillis();
        System.out.println("searchDao.findInItems delta = " + (end - start));
    }

    @Test
    public void testEqualityForMethodFindInItemsForFirstCall() {
        int skip = 0;
        List<Item> actual = searchDao.findInItems(queries, skip, pageSize, null);

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
        List<Item> actual = searchDao.findInItems(queries, skip, pageSize, null);

        assertEquals(20, actual.size());
        assertEquals("1.0552", actual.get(0).getNumber());
        assertEquals("1.0556", actual.get(1).getNumber());
        assertEquals("1.0576", actual.get(9).getNumber());
        assertEquals("1.0606", actual.get(18).getNumber());
        assertEquals("1.0608", actual.get(19).getNumber());
    }

    @Test
    public void testOneWordSearch() {
        List<Item> items = searchDao.findInItems(asList("апокликмия"), 0, pageSize, null);
        assertEquals(2, items.size());
        assertEquals("12.14021", items.get(0).getNumber());
        assertEquals("12.14023", items.get(1).getNumber());
    }

    @Test
    @Ignore // fixme
    public void testFindInItemsWithFilter() {
        int skip = 0;
        List<Item> actual = searchDao.findInItems(queries, skip, pageSize, "3.1201");
        assertEquals(20, actual.size());
        assertEquals("3.1201", actual.get(0).getNumber());
        assertEquals("3.1225", actual.get(1).getNumber());
        assertEquals("10.10037", actual.get(9).getNumber());
        assertEquals("10.10119", actual.get(18).getNumber());
        assertEquals("10.10122", actual.get(19).getNumber());
    }

    @Test
    @Ignore
    public void testOrder() {
        final List<Item> items = searchDao.findInItems(asList("ААИИГЛА-МАА"), 0, pageSize, null);
        @SuppressWarnings("unchecked")
        List<String> numbers = CollectionUtils.transform(items, (Transformer) value -> ((Item) value).getNumber());

        assertEquals(13, numbers.size());
        assertEquals(0, numbers.indexOf("1.1024"));
        assertTrue(numbers.indexOf("10.11809") > numbers.indexOf("3.0056"));
    }
}
