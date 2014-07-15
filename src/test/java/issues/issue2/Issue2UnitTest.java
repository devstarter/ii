package issues.issue2;

import org.apache.commons.io.IOUtils;
import org.ayfaar.app.utils.ItemsCleaner;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class Issue2UnitTest {
    private String valueWithBug;
    private String expectedValue;

    @Before
    public void init() throws IOException {
        valueWithBug = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("dirty-item-3.0089.txt"));
        expectedValue = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("clean-item-3.0089.txt"));
    }

    @Test
    public void equalityItemContent() throws IOException {
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void cleanValueNotNullTest() throws IOException {
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertNotNull(actualValue);
    }

    @Test
    public void emptyString() throws IOException {
        valueWithBug = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("empty-item-3.0089.txt"));
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertNull(actualValue);
    }

    @Test
    public void equalityStringLength() {
        int actualLength = ItemsCleaner.clean(valueWithBug).length();
        assertEquals(expectedValue.length(), actualLength);
    }
}
