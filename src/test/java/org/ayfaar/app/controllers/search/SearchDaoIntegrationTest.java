package org.ayfaar.app.controllers.search;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.assertEquals;

public class SearchDaoIntegrationTest extends IntegrationTest{
    @Inject
    private SearchDao searchDao;

    private final List<String> queries;
    private final String expectedRegexp = "времён|времена|временам|временами|временах|временем|времени|время";


    public SearchDaoIntegrationTest() {
        queries = unmodifiableList(asList("время", "Времени", "Временем", "Временах", "Временами"));
    }

    @Test
    public void testGetByRegexp() {
        List<Item> actual = searchDao.getByRegexp("content", expectedRegexp);

        assertEquals("1.0003", actual.get(0).getNumber());
        assertEquals("1.0008", actual.get(1).getNumber());
        assertEquals("1.0075", actual.get(30).getNumber());
        assertEquals("1.0131", actual.get(53).getNumber());
        assertEquals("1.0846", actual.get(283).getNumber());
    }

    @Test
    public void testCreateRegexp() {
        assertEquals(expectedRegexp, searchDao.createRegexp(queries));
    }

    @Test(timeout = 10000)
    public void testTimeForGettingRequiredItems() {
        searchDao.getByRegexp("content", expectedRegexp);
    }
}
