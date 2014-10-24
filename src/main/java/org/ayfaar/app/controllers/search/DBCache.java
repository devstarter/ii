package org.ayfaar.app.controllers.search;


import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DBCache {
    private String requiredPhrase;
    private String text;
    private UID link;

    @Autowired
    private TermDao termDao;

    @Autowired
    private CategoryDao categoryDao;


    /**
     * Получает Термин и текст из Quote из ResultSearch, или получает Категорию и содержание для нее в виде текста.
     */
    public DBCache(String name, String text, UID uid) {
        this.requiredPhrase = name;
        this.text = text;
        this.link = uid;
    }

    /**
     * Проверяем в базе данных в таблице Cache есть ли там записи для термина или для категории
     */
    public boolean has(String name) {
        return false;
    }

    /**
     * Извлекаем из базы данных DBCache и преобразуем его в SearchResultPage
     */
    public SearchResultPage getSearchResultPage(String name) {
        return null;
    }

    /**
     * Извлекаем из базы данных DBCache и преобразуем его в CategoryPresentation
     */
    public CategoryPresentation getContents(String name) {
        return null;
    }


    public void put(String name, SearchResultPage page) {
        String text = extractQuote(page.getQuotes());
        DBCache cache = new DBCache(name, text, termDao.getByName(name));
        //сохраняем cache в базу данных
    }

    public void put(CategoryPresentation presentation) {
        String content = extractContents(presentation);
        DBCache cache = new DBCache(presentation.getName(), content, categoryDao.get("name", presentation.getName()));
        //сохраняем cache в базу данных
    }

    /**
     * Извлекаем содержимое quotes и создаём из них 1 строку
     */
    private String extractQuote(List<Quote> quotes) {
        return null;
    }

    /**
     * Преобразуем содержание в текст
     */
    private String extractContents(CategoryPresentation presentation) {
        return null;
    }
}
