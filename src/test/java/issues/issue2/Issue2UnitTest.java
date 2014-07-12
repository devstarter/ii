package issues.issue2;

import org.apache.commons.io.IOUtils;
import org.ayfaar.app.utils.ItemsCleaner;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Issue2UnitTest {

    @Test
    public void test1() throws IOException {
        String valueWithBug = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("clean-item-3.0089.txt"));
        String expectedValue = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("dirty-item-3.0089.txt"));
        String actualValue = ItemsCleaner.cleanChapters(valueWithBug);
        assertEquals(expectedValue, actualValue);
    }

    // todo: проверить на null, пустую строку
    // todo: придумать ещё варианты тестов :)
}
