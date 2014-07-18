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
    private String cleanItem3_1052;
    private String dirtyItem3_1052;
    private String cleanItem3_1185;
    private String dirtyItem3_1185;
    private String cleanItem1_0418;
    private String dirtyItem1_0418;
    private String cleanItem10_10865;
    private String dirtyItem10_10865;
    private String cleanItem15_17444;
    private String dirtyItem15_17444;

    @Before
    public void init() throws IOException {
        valueWithBug = getFile("dirty-item-3.0089.txt");
        expectedValue = getFile("clean-item-3.0089.txt");
        cleanItem3_1052 = getFile("clean-item-3.1052.txt");
        dirtyItem3_1052 = getFile("dirty-item-3.1052.txt");
        cleanItem3_1185 = getFile("clean-item-3.1185.txt");
        dirtyItem3_1185 = getFile("dirty-item-3.1185.txt");
        cleanItem1_0418 = getFile("clean-item-1.0418.txt");
        dirtyItem1_0418 = getFile("dirty-item-1.0418.txt");
        cleanItem10_10865 = getFile("clean-item-10.10865.txt");
        dirtyItem10_10865 = getFile("dirty-item-10.10865.txt");
        cleanItem15_17444 = getFile("clean-item-15.17444.txt");
        dirtyItem15_17444 = getFile("dirty-item-15.17444.txt");
    }

    private String getFile(String fileName) throws IOException {
        return IOUtils.toString(Issue2UnitTest.class.getResourceAsStream(fileName));
    }

    @Test
    public void equalityItemContent() throws IOException {
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void equalityItem3_1052Content() throws IOException {
        String actualValue = ItemsCleaner.clean(dirtyItem3_1052);
        assertEquals(cleanItem3_1052, actualValue);
    }

    @Test
    public void equalityItem3_1185Content() throws IOException {
        String actualValue = ItemsCleaner.clean(dirtyItem3_1185);
        assertEquals(cleanItem3_1185, actualValue);
    }

    @Test
    public void equalityItem1_0418Content() throws IOException {
        String actualValue = ItemsCleaner.clean(dirtyItem1_0418);
        assertEquals(cleanItem1_0418, actualValue);
    }

    @Test
    public void equalityItem10_10298Content() throws IOException {
        String actualValue = ItemsCleaner.clean(dirtyItem10_10865);
        assertEquals(cleanItem10_10865, actualValue);
    }

    @Test
    public void equalityItem15_17444Content() throws IOException {
        String actualValue = ItemsCleaner.clean(dirtyItem15_17444);
        assertEquals(cleanItem15_17444, actualValue);
    }

    @Test
    public void cleanValueNotNullTest() throws IOException {
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertNotNull(actualValue);
    }

    @Test
    public void cleanNullValueTest() throws IOException {
        String actualValue = ItemsCleaner.clean(null);
        assertNull(actualValue);
    }

    @Test
    public void emptyString() throws IOException {
        valueWithBug = "";
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertTrue(actualValue.isEmpty());
    }
}
