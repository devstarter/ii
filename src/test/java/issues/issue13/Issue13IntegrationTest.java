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


    //fixme: этот метод должен быть запущен единожды, а не перед каждым запуском.
    // Ведь тесты то запускаются на специальном сервере после каждого комита..
    // представь что будет если кажддый тест будет выполнять подобные долгие операции при каждом запуске...
    @Before
    public void fixQuestionDB() {
        //fixme: загружать можно не все пункты, а только те в которых есть вопрос, а затем по next получать айди следующего пункта




        List<Item> items = itemDao.getAll();
        for (Item item : items) {
            if (item.getContent().contains(ItemsHelper.QUESTION)
                    && (item.getContent().lastIndexOf(ItemsHelper.QUESTION) != 0)) {

                String[] questionAndText = ItemsHelper.removeQuestion(item.getContent());
                item.setContent(questionAndText[0]);
                itemDao.save(item);

                if (item.getNext() != null) {
                    Item nextItem = itemDao.get(item.getNext());
                    nextItem.setContent(ItemsHelper.addQuestion(questionAndText[1], nextItem.getContent()));
                    itemDao.save(nextItem);
                }
            }

        }


//        for (int i = items.size() - 1; i > 0; i--) {
//            String currentContent = items.get(i).getContent();
//            String nextContent = items.get(i-1).getContent();
//            if (nextContent.contains(ItemsHelper.QUESTION) && (nextContent.lastIndexOf(ItemsHelper.QUESTION) != 0)){
//                String[] questionAndText = ItemsHelper.removeQuestion(nextContent);
//                currentContent = ItemsHelper.addQuestion(questionAndText[1],currentContent);
//                items.get(i).setContent(currentContent);
//                itemDao.save(items.get(i));
//                items.get(i-1).setContent(questionAndText[0]);
//                itemDao.save(items.get(i-1));
//            }
//
//
//        }

    }
}
