package issues.issue13;


import org.apache.commons.io.IOUtils;
import org.ayfaar.app.utils.QuestionMover;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Issue13UnitTest {
    private String originValue;
    private String expectedQuestionContent;
    private String expectedCleanContent;

    @Before
    public void init() throws IOException {
        originValue = getFile("origin-item-11.12514.txt");
        expectedQuestionContent = getFile("question-item-11.12515q.txt");
        expectedCleanContent = getFile("clean-item-11.12514.txt");
    }

    private String getFile(String fileName) throws IOException {
        return IOUtils.toString(Issue13UnitTest.class.getResourceAsStream(fileName));
    }

    @Test
    public void isNewItemContainQuestion() {
        String actualValue = QuestionMover.extractQuestion(originValue);
        assertEquals(expectedQuestionContent, actualValue);
    }

    @Test
    public void isOldItemWithoutQuestion() {
        String actualValue = QuestionMover.cleanQuestion(originValue);
        assertEquals(expectedCleanContent, actualValue);
    }

    @Test
    public void testExtractMethodIfOriginValueNull() {
        String actualValue = QuestionMover.extractQuestion(null);
        assertNull(actualValue);
    }

    @Test
    public void testCleanMethodIfOriginValueNull() {
        String actualValue = QuestionMover.cleanQuestion(null);
        assertNull(actualValue);
    }

    @Test
    public void testExtractMethodIfOriginValueEmpty() {
        String emptyValue = "";
        String actualValue = QuestionMover.extractQuestion(emptyValue);
        assertTrue(actualValue.isEmpty());
    }

    @Test
    public void testCleanMethodIfOriginValueEmpty() {
        String emptyValue = "";
        String actualValue = QuestionMover.extractQuestion(emptyValue);
        assertTrue(actualValue.isEmpty());
    }
}
