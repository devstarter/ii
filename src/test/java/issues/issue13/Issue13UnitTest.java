package issues.issue13;

import org.ayfaar.app.AbstractTest;
import org.ayfaar.app.utils.ItemsHelper;
import org.junit.Test;

import java.io.IOException;

import static org.ayfaar.app.utils.StringUtils.removeAllNewLines;
import static org.junit.Assert.*;

public class Issue13UnitTest extends AbstractTest {

    private final String cleanItem15_17819;
    private final String dirtyItem15_17819;
    private final String question15_17819;
    private final String itemWithQuestion15_17820;
    private final String itemWithoutQuestion15_17820;

    private final String cleanItem13_15325;
    private final String dirtyItem13_15325;
    private final String question13_15325;

    public Issue13UnitTest() throws IOException {
        // наполняем эти переменные едонажды при создании инстанса теста, а не перед каждым тестом
        // помечаем их final, чтобы случайно не изменить внутри тестов
        cleanItem15_17819 = getFile("clean-item-15.17819.txt");
        dirtyItem15_17819 = getFile("dirty-item-15.17819.txt");
        question15_17819 = getFile("item-15.17820-question.txt");
        itemWithQuestion15_17820 = getFile("item-15.17820-with-question.txt");
        itemWithoutQuestion15_17820 = getFile("item-15.17820-without-question.txt");

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
        String result = ItemsHelper.addQuestion(question15_17819, itemWithoutQuestion15_17820);
        // need removeAllNewLines for testing on unix server, has some problems with comparing new lines
        assertEquals(removeAllNewLines(itemWithQuestion15_17820), removeAllNewLines(result));
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
}
