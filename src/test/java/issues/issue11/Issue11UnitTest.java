package issues.issue11;

import org.ayfaar.app.UnitTest;
import org.ayfaar.app.utils.ItemsCleaner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Issue11UnitTest extends UnitTest {
    private String cleanItem11_13017;
    private String dirtyItem11_13017;

    @Before
    public void init() throws IOException {
        // todo: создать эти файлы в /src/test/resources/issues/issue11
        cleanItem11_13017 = getFile("clean-item-11.13017.txt");
        dirtyItem11_13017 = getFile("dirty-item-11.13017.txt");
    }

    @Test
    public void equalityItemContent() throws IOException {
        String actualValue = ItemsCleaner.clean(dirtyItem11_13017);
        assertEquals(cleanItem11_13017, actualValue);
    }


    @Test
    public void cleanNullValueTest() throws IOException {
        String actualValue = ItemsCleaner.clean(null);
        assertNull(actualValue);
    }

    @Test
    public void emptyString() throws IOException {
        String actualValue = ItemsCleaner.clean("");
        Assert.assertEquals("", actualValue);
    }
}
