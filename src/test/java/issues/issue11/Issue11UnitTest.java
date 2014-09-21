package issues.issue11;

import org.ayfaar.app.AbstractTest;
import org.ayfaar.app.utils.ItemsHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Issue11UnitTest extends AbstractTest {
    private String cleanItem11_13017;
	private String dirtyItem11_13017;
    private String dirtyItemWithStars;
    private String cleanItemWithStars;

	@Before
    public void init() throws IOException {
        cleanItem11_13017 = getFile("clean-item-11.13017.txt");
	    dirtyItem11_13017 = getFile("dirty-item-11.13017.txt");
        dirtyItemWithStars = getFile("dirty-item-contain-stars.txt");
        cleanItemWithStars = getFile("clean-item-contain-stars.txt");
	}

	@Test
 	public void equalityItemContent() throws IOException {
        String actualValue = ItemsHelper.cleanFootnote(dirtyItem11_13017);
        assertEquals(cleanItem11_13017, actualValue);
	}

    @Test
 	public void cleanFootnoteNullValueTest() throws IOException {
        String actualValue = ItemsHelper.cleanFootnote(null);
        assertNull(actualValue);
    }

   	@Test
 	 public void testCleanFootnoteEmptyString() throws IOException {
         String actualValue = ItemsHelper.cleanFootnote("");
 	     Assert.assertEquals("", actualValue);
     }

    @Test
    public void testCleanFootnoteStarNotDeleteEnum() throws IOException {
        String actualValue = ItemsHelper.cleanFootnoteStar(dirtyItemWithStars);
        assertEquals(cleanItemWithStars, actualValue);
    }

    @Test
    public void testCleanFootnoteStarNullValue() throws IOException {
        String actualValue = ItemsHelper.cleanFootnoteStar(null);
        assertNull(actualValue);
    }

    @Test
    public void testCleanFootnoteStarEmptyString() throws IOException {
        String actualValue = ItemsHelper.cleanFootnoteStar("");
        Assert.assertEquals("", actualValue);
    }
}
