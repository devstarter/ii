package issues.issue13;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsHelper;
import org.hibernate.criterion.MatchMode;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Issue13IntegrationTest extends IntegrationTest {

    private boolean run = false;

    @Autowired ItemDao itemDao;

    @Test
    /**
     * Удостоверяемся что в базе данных лежит уже очищеный вариант 15.17819 абзаца
     */
    public void testItem15_17819() throws IOException {
        Item item = itemDao.getByNumber("15.17819");
        assertEquals(getFile("clean-item-15.17819.txt"), item.getContent());
    }

    @Test
    /**
     * Удостоверяемся что в базе данных в 15.17820 абзаце есть вопрос, перенесённый из 15.17819
     */
    public void testItem15_17820() throws IOException {
        Item item = itemDao.getByNumber("15.17820");
        assertEquals(getFile("item-15.17820-with-question.txt"), item.getContent());
    }

    @Test
    /**
     * проверяем что все вопросы во всех абзацах находятся в начале,
     * то есть нет вопросов, перед которыми бы был символ перевода каретки (новая строка)
     */
    public void allQuestions() {
        List<Item> items = itemDao.getLike("content", "\n"+ItemsHelper.QUESTION, MatchMode.ANYWHERE);
        assertTrue(items.isEmpty());
    }

    @Before
    public void fixQuestionDB() {

        if (!run) {
            List<Item> items = itemDao.getLike("content", "\n" + ItemsHelper.QUESTION, MatchMode.ANYWHERE);

            for (Item item : items) {
                String[] questionAndText = ItemsHelper.removeQuestion(item.getContent());
                item.setContent(questionAndText[0]);
                itemDao.save(item);

                if (item.getNext() != null) {
                    Item nextItem = itemDao.get(item.getNext());
                    nextItem.setContent(ItemsHelper.addQuestion(questionAndText[1], nextItem.getContent()));
                    itemDao.save(nextItem);
                }
            }
            run = true;
        }
    }
}
