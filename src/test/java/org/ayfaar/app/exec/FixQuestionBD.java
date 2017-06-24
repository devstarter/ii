package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestDevConfiguration;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class FixQuestionBD {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestDevConfiguration.class);
        ItemDao itemDao = ctx.getBean(ItemDao.class);

        List<Item> items = itemDao.getByRegexp("content","^.+" + ItemsHelper.QUESTION);

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

    }
}
