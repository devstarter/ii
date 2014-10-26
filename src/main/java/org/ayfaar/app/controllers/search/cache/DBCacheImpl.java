package org.ayfaar.app.controllers.search.cache;


import org.springframework.stereotype.Component;


@Component
public class DBCacheImpl implements DBCache {
    /**
     * Сохраняем в базу данных содержание или результаты поиска
     */
    @Override
    public void save(JsonEntity json) {

    }

    /**
     * По имени термина или имени категории проверяем есть ли в таблице cache результаты старого поиска или содержание
     */
    @Override
    public boolean has(String name) {
        return false;
    }

    /**
     * Извлекаем из базы данных содержание или результаты старого поиска
     */
    @Override
    public JsonEntity get(String name) {
        return null;
    }


    @Override
    public void clean() {

    }
}
