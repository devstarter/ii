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

    @Before
    public void init() throws IOException {
        valueWithBug = getFile("dirty-item-3.0089.txt");
        expectedValue = getFile("clean-item-3.0089.txt");
        cleanItem3_1052 = getFile("clean-item-3.1052.txt");
        dirtyItem3_1052 = getFile("dirty-item-3.1052.txt");
        cleanItem3_1185 = getFile("clean-item-3.1185.txt");
        dirtyItem3_1185 = getFile("dirty-item-3.1185.txt");
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
        valueWithBug = IOUtils.toString(Issue2UnitTest.class.getResourceAsStream("empty-item-3.0089.txt"));
        //? зачем тебе пустой файл ведь можно просто передать пустую строку ""
        String actualValue = ItemsCleaner.clean(valueWithBug);
        assertNull(actualValue);
    }

    @Test
    public void equalityStringLength() {
        int actualLength = ItemsCleaner.clean(valueWithBug).length();
        assertEquals(expectedValue.length(), actualLength);
    }
}
