package issues.issue13;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsHelper;
import org.hibernate.criterion.MatchMode;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Issue13IntegrationTest extends IntegrationTest {

    @Autowired ItemDao itemDao;

    @Test
    /**
     * Удостоверяемся что в базе данных лежит уже очищеный вариант 15.17819 абзаца
     */
    public void testItem15_17819() throws IOException {
        Item item = itemDao.getByNumber("15.17819");
        //assertEquals(getFile("clean-item-15.17819.txt"), item.getContent());
    }

    @Test
    /**
     * Удостоверяемся что в базе данных в 15.17820 абзаце есть вопрос, перенесённый из 15.17819
     */
    public void testItem15_17820() throws IOException {
        Item item = itemDao.getByNumber("15.17820");
        //assertEquals(getFile("item-15.17820-with-question.txt"), item.getContent());
    }

    @Test
    /**
     * проверяем что все вопросы во всех абзацах находятся в начале,
     * то есть нет вопросов, перед которыми бы был символ перевода каретки (новая строка)
     */
    public void allQuestions() {
        List<Item> items = itemDao.getLike("content", "\n"+ItemsHelper.QUESTION, MatchMode.ANYWHERE);
        //assertTrue(items.isEmpty());
    }

    //todo: написать метод изменеия базы данных, пример: issues.issue2.Issue2IntegrationTest.fixQuestionDB()

    @Before
    public void fixQuestionDB() {
        List<Item> items = itemDao.getAll();
        for (int i = items.size() - 1; i > 0; i--) {
            String currentContent = items.get(i).getContent();
            String nextContent = items.get(i-1).getContent();
            if (nextContent.contains(ItemsHelper.QUESTION)){
                String[] questionAndText = ItemsHelper.removeQuestion(nextContent);
                currentContent = ItemsHelper.addQuestion(questionAndText[0],questionAndText[1]);
                items.get(i).setContent(currentContent);
                itemDao.save(items.get(i));
                items.get(i-1).setContent(questionAndText[1]);
                itemDao.save(items.get(i-1));
            }


        }

    }
}
