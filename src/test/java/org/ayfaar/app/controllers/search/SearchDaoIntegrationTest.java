package org.ayfaar.app.controllers.search;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.*;

@Ignore
public class SearchDaoIntegrationTest extends IntegrationTest{
    @Inject
    private SearchDao searchDao;

    @Inject
    private NewSearchController controller;

    private List<Item> items;
    private List<String> queries;

    @Before
    public void init() throws IOException {
        Item item_1_0003 = new Item("1.0003", getFile("item-1.0003.txt"));
        Item item_1_0008 = new Item("1.0008", getFile("item-1.0008.txt"));
        Item item_1_0075 = new Item("1.0075", getFile("item-1.0075.txt"));
        Item item_1_0846 = new Item("1.0846", getFile("item-1.0846.txt"));
        Item item_1_0131 = new Item("1.0131", getFile("item-1.0131.txt"));
        items = unmodifiableList(asList(item_1_0003, item_1_0008, item_1_0075, item_1_0131, item_1_0846));
        queries = asList("время", "Времени", "Временем", "Временах", "Временами");
    }


    @Test
    public void test() {
        //String regexp = "(Время)|(Времени)|(Временем)|(Временах)|(Временами)|(время)|(времени)|(временем)|(временах)|(временами)";
        String regexp = controller.createRegexp(queries);
        List<Item> actual = searchDao.getByRegexp("content", regexp);

        assertEquals(items.get(0).getNumber(), actual.get(0).getNumber());
        assertEquals(items.get(1).getNumber(), actual.get(1).getNumber());
        assertEquals(items.get(2).getNumber(), actual.get(30).getNumber());
        assertEquals(items.get(3).getNumber(), actual.get(53).getNumber());
        assertEquals(items.get(4).getNumber(), actual.get(283).getNumber());
    }
}
