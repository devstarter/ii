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
        // думаю тут можно выборочно прверить
        assertEquals("1.0003", actual.get(0).getNumber());
        assertEquals("1.0008", actual.get(1).getNumber());
        assertEquals("1.0010", actual.get(2).getNumber());
        assertEquals("1.0013", actual.get(3).getNumber());
        assertEquals("1.0014", actual.get(4).getNumber());
        assertEquals("1.0018", actual.get(5).getNumber());
        assertEquals("1.0020", actual.get(6).getNumber());
        assertEquals("1.0022", actual.get(7).getNumber());
        assertEquals("1.0025", actual.get(8).getNumber());
        assertEquals("1.0028", actual.get(9).getNumber());
        assertEquals("1.0029", actual.get(10).getNumber());
        assertEquals("1.0030", actual.get(11).getNumber());
        assertEquals("1.0031", actual.get(12).getNumber());
        assertEquals("1.0038", actual.get(13).getNumber());
        assertEquals("1.0039", actual.get(14).getNumber());
        assertEquals("1.0041", actual.get(15).getNumber());
        assertEquals("1.0043", actual.get(16).getNumber());
        assertEquals("1.0047", actual.get(17).getNumber());
        assertEquals("1.0048", actual.get(18).getNumber());
        assertEquals("1.0050", actual.get(19).getNumber());
    }

    @Test
    public void testEqualityForMethodFindInItemsForTenthCall() {
        int skip = pageSize * 10;
        List<Item> actual = searchDao.findInItems(queries, skip, pageSize);

        // думаю тут можно выборочно прверить
        assertEquals("1.0552", actual.get(0).getNumber());
        assertEquals("1.0556", actual.get(1).getNumber());
        assertEquals("1.0560", actual.get(2).getNumber());
        assertEquals("1.0561", actual.get(3).getNumber());
        assertEquals("1.0564", actual.get(4).getNumber());
        assertEquals("1.0565", actual.get(5).getNumber());
        assertEquals("1.0566", actual.get(6).getNumber());
        assertEquals("1.0567", actual.get(7).getNumber());
        assertEquals("1.0574", actual.get(8).getNumber());
        assertEquals("1.0576", actual.get(9).getNumber());
        assertEquals("1.0577", actual.get(10).getNumber());
        assertEquals("1.0582", actual.get(11).getNumber());
        assertEquals("1.0590", actual.get(12).getNumber());
        assertEquals("1.0595", actual.get(13).getNumber());
        assertEquals("1.0598", actual.get(14).getNumber());
        assertEquals("1.0600", actual.get(15).getNumber());
        assertEquals("1.0601", actual.get(16).getNumber());
        assertEquals("1.0605", actual.get(17).getNumber());
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
