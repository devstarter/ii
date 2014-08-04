package org.ayfaar.app.controllers.search;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.criterion.MatchMode;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.assertEquals;

public class SearchDaoIntegrationTest extends IntegrationTest{
    @Inject
    private SearchDao searchDao;

    private final List<String> queries;
    private final String expectedRegexp = "времён|времена|временам|временами|временах|временем|времени|время";


    public SearchDaoIntegrationTest() {
        queries = unmodifiableList(asList("времён", "времена", "временам", "временами", "временах", "временем", "времени", "время"));
    }

    @Test(/*timeout = 10000*/)
    public void testTimeForMethodFindInItems() {
        List<Item> items = new ArrayList<Item>();

        long start = System.currentTimeMillis();
        items = searchDao.findInItems(queries);
        long end = System.currentTimeMillis();

        System.out.println("delta = " + (end - start));
    }

    @Test(/*timeout = 10000*/)
    public void testTimeForMethodFindInItems2() {
        List<Item> items = new ArrayList<Item>();

        long start = System.currentTimeMillis();
        items = searchDao.findInItems2(queries);
        long end = System.currentTimeMillis();

        System.out.println("delta = " + (end - start));
    }

    @Test(/*timeout = 10000*/)
    public void testTimeForRegexp() {
        String expectedRegexp = "времён|времена|временам|временами|временах|временем|времени|время|Времён|Времена|Временам|Временами|Временах|Временем|Времени|Время";

        long start = System.currentTimeMillis();
        List<Item> items = searchDao.getByRegexp("content", expectedRegexp);
        long end = System.currentTimeMillis();

        System.out.println("delta = " + (end - start));
    }

    @Test(/*timeout = 10000*/)
    public void testTimeForGetLike() {
        List<Item> items = new ArrayList<Item>();
        long start = System.currentTimeMillis();
        for(String query : queries) {
            items.addAll(searchDao.getLike("content", query, MatchMode.ANYWHERE));
        }

        Set<Item> set = new HashSet<Item>(items);
        List<Item> newItems = new ArrayList<Item>(set);
        searchDao.sort(newItems);
        long end = System.currentTimeMillis();

        System.out.println("delta = " + (end - start));
    }



   /* @Test
    public void testGetByRegexp() {
        List<Item> actual = searchDao.getByRegexp("content", expectedRegexp);

        assertEquals("1.0003", actual.get(0).getNumber());
        assertEquals("1.0008", actual.get(1).getNumber());
        assertEquals("1.0075", actual.get(30).getNumber());
        assertEquals("1.0131", actual.get(53).getNumber());
        assertEquals("1.0846", actual.get(283).getNumber());
    }*/

    /*@Test
    public void testCreateRegexp() {
        //assertEquals(expectedRegexp, searchDao.createRegexp(queries));
    }*/

    /*@Test(timeout = 10000)
     public void testTimeForGettingRequiredItems() {
        //searchDao.getByRegexp("content", expectedRegexp);
        List<String> queries = Arrays.asList("времён", "времена", "временам",
                "временами", "временах", "временем", "времени", "время");

        List<Item> items = new ArrayList<Item>();
        for(String s : queries) {
            items.addAll(searchDao.getLike("content", s, MatchMode.ANYWHERE));
        }
        List<Item> list = searchDao.sort(items);
        System.out.println("list = " + list.size());
        for(Item i : list) {
            System.out.println("item = " + i.getNumber());
        }
    }*/
}
