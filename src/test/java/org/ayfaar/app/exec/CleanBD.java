package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestDevConfiguration;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.utils.ItemsHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class CleanBD {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestDevConfiguration.class);
        ItemDao itemDao = ctx.getBean(ItemDao.class);
        LinkDao linkDao = ctx.getBean(LinkDao.class);

        List<Item> items = itemDao.getAll();
        for(Item item : items) {
            String clean = ItemsHelper.clean(item.getContent());
            if (!clean.equals(item.getContent())) {
                System.out.println(item.getNumber());
                System.out.println(item.getContent());
                System.out.println(clean);
                item.setContent(clean);
                itemDao.save(item);
            }
        }
        for (Link link : linkDao.getAll()) {
            if (link.getQuote() != null) {
                String clean = ItemsHelper.clean(link.getQuote());
                if (!clean.equals(link.getQuote())) {
                    System.out.println(link.getQuote());
                    System.out.println(clean);
                    link.setQuote(clean);
                    linkDao.save(link);
                }
            }
        }

    }
}
