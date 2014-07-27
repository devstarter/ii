package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class CleanBD {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        ItemDao itemDao = ctx.getBean(ItemDao.class);

        List<Item> items = itemDao.getAll();
        for(Item item : items) {
            String clean = ItemsHelper.clean(item.getContent());
            if (!clean.equals(item.getContent())) {
                item.setContent(clean);
                itemDao.save(item);
            }
        }
    }
}
