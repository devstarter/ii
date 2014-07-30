package org.ayfaar.app.controllers;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.controllers.search.HandleItems;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchFilter;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

//todo пометить как контролер и зделать доступнім по адресу "v2/search"
public class NewSearchController {
    @Autowired
    private HandleItems handleItems;

    private List<String> allPossibleSearchQueries = new ArrayList<String>();


    /**
     * Поиск будет производить только по содержимому Item
     * todo сделать этот метод доступным через веб
     *
     * @param pageNumber номер страницы
     */
    public SearchResultPage search(String query, Integer pageNumber, SearchFilter filter) {
        // 1. Очищаем введённую фразу от лишних пробелов по краям и переводим в нижний регистр
        query = prepareQuery(query);

        // 2. Проверяем есть ли кеш, если да возвращаем его
        if (hasCached(query, pageNumber, filter)) {
            return getCache(query, pageNumber, filter);
        }

        SearchResultPage page = new SearchResultPage();

        // 3. Определить термин ли это
        Term term = getTerm(query);
        // если нет поискать в разных падежах
        if (term == null) {
            term = findTermInMorphs(query);
        }

        // 3.1. Если да, Получить все синониме термина
        List<Item> foundItems;

        if (term != null) {
            List<Term> allSearchTerms = getAllAliases(term);

            // 3.2. Получить все падежи по всем терминам
            allPossibleSearchQueries = getAllMorphs(allSearchTerms);
            // 4. Произвести поиск по списку синонимов слов
            foundItems = searchInDb(query, allPossibleSearchQueries, pageNumber, filter);
        } else {
            // 4. Поиск фразы (не термин)
            foundItems = searchInDb(query, null, pageNumber, filter);
        }

        page.setHasMore(false);

        // 5. Обработка найденных пунктов
        List<Quote> quotes = handleItems.createQuotes(foundItems, allPossibleSearchQueries);
        page.setQuotes(quotes);

        // 6. Вернуть результат
        return page;
    }

    private List<Item> searchInDb(String query, List<String> words, Integer page, SearchFilter filter) {
        // 4.1. Результат должен быть отсортирован:
        // а. В первую очередь должны быть точные совпадения
        // б. Сначала самые ранние пункты

        // 4.2. В результате нужно знать есть ли ещё результаты поиска для следующей страницы
        throw new NotImplementedException();
    }

    private List<String> getAllMorphs(List<Term> terms) {
        throw new NotImplementedException();
    }

    private List<Term> getAllAliases(Term term) {
        throw new NotImplementedException();
    }

    private Term findTermInMorphs(String query) {
        throw new NotImplementedException();
    }

    private Term getTerm(String query) {
        throw new NotImplementedException();
    }

    private SearchResultPage getCache(String query, Integer page, SearchFilter filter) {
        throw new NotImplementedException();
    }

    private boolean hasCached(String query, Integer page, SearchFilter filter) {
        throw new NotImplementedException();
    }

    private String prepareQuery(String query) {
        throw new NotImplementedException();
    }
}
