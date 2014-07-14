package issues.issue2;

import org.apache.commons.io.IOUtils;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsCleaner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Issue2UnitTest {
    private String valueWithBug;
    private String expectedValue;

    @Before
    public void init() throws IOException {
        valueWithBug = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("dirty-item-3.0089.txt"));
        expectedValue = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("clean-item-3.0089.txt"));
    }

    @Test
    public void test1() throws IOException {
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void cleanValueNotNullTest() throws IOException {
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertNotNull(expectedValue, actualValue);
    }

    // todo: проверить на null, пустую строку
    // todo: придумать ещё варианты тестов :)
}
