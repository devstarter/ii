package org.ayfaar.app.controllers.search;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class SearchDaoIntegrationTest extends IntegrationTest{
    @Inject
    private SearchDao searchDao;

    private List<String> expectedNumbers;
    private List<String> queries;
    private String regexp;

    @Before
    public void init() throws IOException {
        expectedNumbers = new ArrayList<String>();
        expectedNumbers.add("1.0003");
        expectedNumbers.add("1.0008");
        expectedNumbers.add("1.0075");
        expectedNumbers.add("1.0131");
        expectedNumbers.add("1.0846");

        queries = asList("время", "Времени", "Временем", "Временах", "Временами");

        regexp = searchDao.createRegexp(queries);
    }

    @Test
    public void testCreateRegexp() {
        String expected = "времён|времена|временам|временами|временах|временем|времени|время";
        assertEquals(expected, searchDao.createRegexp(queries));
    }

    @Test
    public void testGetByRegexp() {
        List<Item> actual = searchDao.getByRegexp("content", regexp);

        // сравнивать можно просто по номерам абзацев. не обязательно хранить объект весь Item
        assertEquals(expectedNumbers.get(0), actual.get(0).getNumber());
        assertEquals(expectedNumbers.get(1), actual.get(1).getNumber());
        assertEquals(expectedNumbers.get(2), actual.get(30).getNumber());
        assertEquals(expectedNumbers.get(3), actual.get(53).getNumber());
        assertEquals(expectedNumbers.get(4), actual.get(283).getNumber());
    }

    @Test(timeout = 10000)
    public void testTimeForGettingRequiredItems() {
        List<Item> actual = searchDao.getByRegexp("content", regexp);
    }
}
