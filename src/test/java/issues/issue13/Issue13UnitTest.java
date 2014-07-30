package issues.issue13;

import org.apache.commons.io.IOUtils;
import org.ayfaar.app.utils.ItemsHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class Issue13UnitTest {

    private String cleanItem15_17819;
    private String dirtyItem15_17819;
    private String question15_17819;
    private String itemWithQuestion15_17819;
    private String itemWithoutQuestion15_17819;

    private String cleanItem13_15325;
    private String dirtyItem13_15325;
    private String question13_15325;

    @Before
    public void init() throws IOException {
        cleanItem15_17819 = getFile("clean-item-15.17819.txt");
        dirtyItem15_17819 = getFile("dirty-item-15.17819.txt");
        question15_17819 = getFile("item-15.17820-question.txt");
        itemWithQuestion15_17819 = getFile("item-15.17820-with-question.txt");
        itemWithoutQuestion15_17819 = getFile("item-15.17820-without-question.txt");

        dirtyItem13_15325 = getFile("dirty-item-13.15325.txt");
        cleanItem13_15325 = getFile("clean-item-13.15325.txt");
        question13_15325 = getFile("item-13.15325-question.txt");
    }

    @Test
    public void removeQuestionTest() throws IOException {
        String[] parts = ItemsHelper.removeQuestion(dirtyItem15_17819);

        assertEquals(2, parts.length);
        assertTrue(!parts[0].contains(ItemsHelper.QUESTION));
        assertEquals(cleanItem15_17819, parts[0]);

        assertTrue(parts[1].indexOf(ItemsHelper.QUESTION) == 0);
        assertEquals(question15_17819, parts[1]);


        parts = ItemsHelper.removeQuestion(dirtyItem13_15325);

        assertEquals(2, parts.length);
        assertTrue(!parts[0].contains(ItemsHelper.QUESTION));
        assertEquals(cleanItem13_15325, parts[0]);

        assertTrue(parts[1].indexOf(ItemsHelper.QUESTION) == 0);
        assertEquals(question13_15325, parts[1]);


    }

    @Test
    public void addQuestionTest() throws IOException {
        String result = ItemsHelper.addQuestion(question15_17819, itemWithoutQuestion15_17819);
        assertEquals(itemWithQuestion15_17819, result);
    }


    @Test
    public void cleanNullValueTest() throws IOException {
        assertNull(ItemsHelper.removeQuestion(null));
        assertNull(ItemsHelper.addQuestion(null, null));
    }

    @Test
    public void emptyString() throws IOException {
        String[] parts = ItemsHelper.removeQuestion("");
        assertEquals(2, parts.length);
        assertTrue(parts[0].isEmpty());

        String result = ItemsHelper.addQuestion("", "");
        assertTrue(result.isEmpty());
    }

    private String getFile(String fileName) throws IOException {
        return IOUtils.toString(Issue13UnitTest.class.getResourceAsStream(fileName));
    }
}
