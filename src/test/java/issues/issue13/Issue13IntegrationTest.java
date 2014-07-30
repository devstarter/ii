package issues.issue13;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.hibernate.criterion.MatchMode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.ayfaar.app.utils.ItemsHelper.QUESTION;
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
        List<Item> items = itemDao.getLike("content", "\n"+ QUESTION, MatchMode.ANYWHERE);
        items.addAll(itemDao.getByRegexp("content", "^.+"+QUESTION));
        System.out.println(items.size());
        for (Item item : items) {
            System.out.println(item.getNumber());
        };
        assertTrue(items.isEmpty());
    }
}
