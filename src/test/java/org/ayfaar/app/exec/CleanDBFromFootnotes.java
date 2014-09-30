package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class CleanDBFromFootnotes {
    public static void main(String[] args) {
        ApplicationContext apc = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        ItemDao itemDao = apc.getBean(ItemDao.class);

        List<Item> items = itemDao.getAll();
        for(Item item : items) {
            String clean = ItemsHelper.cleanFootnote(item.getContent());
            clean = ItemsHelper.cleanFootnoteStar(clean);
            if (!clean.equals(item.getContent())) {
                System.out.println(item.getNumber());
                System.out.println(item.getContent());
                System.out.println(clean);
                item.setContent(clean);
                itemDao.save(item);
            }
        }
    }
}
