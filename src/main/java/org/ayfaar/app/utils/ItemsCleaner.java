package org.ayfaar.app.utils;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;

import java.util.List;

public class ItemsCleaner {
    public static String clean(String value) {
        String[] str = value.split("\n");

        if(value.length() == 0) {
            return null;
        }
        return str[0].trim();
    }

    public static void cleanDB(ItemDao dao) {
        List<Item> items = dao.getAll();
        for(Item item : items) {
            item.setContent(clean(item.getContent()));
            dao.save(item);
        }
    }
}
