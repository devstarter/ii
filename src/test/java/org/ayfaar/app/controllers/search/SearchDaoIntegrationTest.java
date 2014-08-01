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
import static org.junit.Assert.assertEquals;

public class SearchDaoIntegrationTest extends IntegrationTest{
    @Inject
    private SearchDao searchDao;

    private List<String> queries;
    private final String expectedRegexp = "времён|времена|временам|временами|временах|временем|времени|время";

    @Before
    public void init() throws IOException {
        // fixme сделать не изменяемым списком, что бы случайно не изменить в тестах
        queries = asList("время", "Времени", "Временем", "Временах", "Временами");

        // не верно проверять результатом работы не стабильной функции, лучше просто текстом (expectedRegexp)
        //regexp = searchDao.createRegexp(queries);
    }

    @Test
    public void testCreateRegexp() {
        String expected = "времён|времена|временам|временами|временах|временем|времени|время";
        assertEquals(expected, searchDao.createRegexp(queries));
    }

    @Test
    public void testGetByRegexp() {
        List<Item> actual = searchDao.getByRegexp("content", expectedRegexp);

        // так ведь наглядней...
        assertEquals("1.0003", actual.get(0).getNumber());
        assertEquals("1.0008", actual.get(1).getNumber());
        assertEquals("1.0075", actual.get(30).getNumber());
        assertEquals("1.0131", actual.get(53).getNumber());
        assertEquals("1.0846", actual.get(283).getNumber());
    }

<<<<<<< HEAD
=======
    @Test
    public void testCreateRegexp() {
        assertEquals(expectedRegexp, searchDao.createRegexp(queries));
    }

>>>>>>> 9c5d13de7cffdc984882f6594dcb4e9e5768dcf9
    @Test(timeout = 10000)
    public void testTimeForGettingRequiredItems() {
        searchDao.getByRegexp("content", expectedRegexp);
    }
}
