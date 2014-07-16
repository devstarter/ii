package org.ayfaar.app.utils;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;

import java.util.List;

/**
 * Очистка пунктов от сносок, названий Глав и Разделов
 *
 * issue2 https://github.com/devstarter/ii/issues/2
 * issue3 https://github.com/devstarter/ii/issues/3
 */
public class ItemsCleaner {
    public static String clean(String value) {
        String[] str = value.split("\n");

        if(value.length() == 0) {
            return null;
        }
        return str[0].trim();
    }

    // fixme: Предлагаю перенести весь этот метод в интеграционный тест, так как работа с базой данных выходит за
    // рамки ответственности этого класса. Я предлагаю что бы этот клас занмался только обработкой текста пунктов.
    public static void cleanDB(ItemDao dao) {
        List<Item> items = dao.getAll();
        for(Item item : items) {
            item.setContent(clean(item.getContent()));
            dao.save(item);
        }
    }
}
