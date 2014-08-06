package org.ayfaar.app.controllers.search;

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

    @Test
    public void testFilter() {
        // todo тестировать с фильтрацией начального пункта
    }
}
