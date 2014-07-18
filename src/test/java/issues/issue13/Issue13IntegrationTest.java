package issues.issue13;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.QuestionMover;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Issue13IntegrationTest extends IntegrationTest {

    @Value("#{T(org.apache.commons.io.FileUtils).readFileToString(" +
            "T(org.springframework.util.ResourceUtils).getFile('classpath:issues/issue13/origin-item-11.12514.txt'))}")
    String expectedContent;

    @Autowired
    ItemDao itemDao;

    @Test
    public void moveQuestionsToAnotherItem() {
        /*List<Item> items = itemDao.getAll();
        for(Item item : items) {
            String content = QuestionMover.extractQuestion(item.getContent());
            System.out.println(content);
        }*/
        Item item = itemDao.getByNumber("11.12514");
        String question = QuestionMover.extractQuestion(item.getContent());
        String rest = QuestionMover.cleanQuestion(item.getContent());
        System.out.println(question);
        System.out.println(rest);

        Item questionItem = new Item();
        questionItem.setContent(question);
        String number = item.getNumber();
        createQuestionItem(question, number);

    }

    private void createQuestionItem(String question, String number) {
        Item questionItem = new Item();
        double oldNumber = Double.parseDouble(number);
        String newNumber  = (oldNumber + 1) + "q";
        questionItem.setContent(question);
        System.out.println(oldNumber);
        System.out.println(newNumber);
        //questionItem.setNumber();
    }

    private void parseNumber() {

    }


    @Test
    public void checkParticularItem() {
        Item item = itemDao.getByNumber("11.12514");
        assertEquals(expectedContent, item.getContent());
    }


}
