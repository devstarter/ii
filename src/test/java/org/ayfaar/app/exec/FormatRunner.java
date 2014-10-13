package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.format.FormatItems;
import org.ayfaar.app.model.Item;
import org.hibernate.criterion.MatchMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FormatRunner {

    public static void main(String[] args) throws IOException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        ItemDao itemDao = ctx.getBean(ItemDao.class);

        FormatItems.open(FormatItems.class.getResourceAsStream("10tom.html"));

        final List<Item> items = itemDao.getLike("number", "10.", MatchMode.START);

        final File file = File.createTempFile("items", "");

        StringBuilder sb = new StringBuilder();

        for (Item item : items) {
            if (item.getNumber().equals("10.11895")) break;
            System.out.println(item.getNumber());
            FormatItems.getItemHtmlElement(item.getNumber());
            sb.append(item.getNumber()).append(". ");
            sb.append(FormatItems.format(item.getNumber()));
            sb.append("\n\n");
        }
        final String html = sb.toString();
    }


}
