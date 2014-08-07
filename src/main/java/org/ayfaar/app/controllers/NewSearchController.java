package org.ayfaar.app.controllers;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;

//todo пометить как контролер и зделать доступнім по адресу "v2/search"
public class NewSearchController {
    public static final int PAGE_SIZE = 20;
    @Inject
    private SearchQuotesHelper handleItems;

    @Inject
    private SearchDao searchDao;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Inject
    private SearchCache cache;

    private List<String> searchQueries;


    /**
     * Поиск будет производить только по содержимому Item
     * todo сделать этот метод доступным через веб
     *
     * @param pageNumber номер страницы
     */
    public SearchResultPage search(String query, Integer pageNumber, String fromItemNumber) {
        // 1. Очищаем введённую фразу от лишних пробелов по краям и переводим в нижний регистр
        query = prepareQuery(query);

        // 2. Проверяем есть ли кеш, если да возвращаем его
        Object cacheKey = cache.generateKey(query, pageNumber, fromItemNumber);
        if (cache.has(cacheKey)) {
            return cache.get(cacheKey);
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
        // указывает сколько результатов поиска нужно пропустиьб, то есть когда ищем следующую страницу
        int skipResults = pageNumber*PAGE_SIZE;

        if (term != null) {
            // 3.2. Получить все падежи по всем терминам
            searchQueries = getAllMorphs(term);
            // 4. Произвести поиск
            // 4.1. Сначала поискать совпадение термина в различных падежах
            foundItems = searchDao.searchInDb(searchQueries, skipResults, PAGE_SIZE, fromItemNumber);
            // 4.2. Если количества не достаточно для заполнения страницы то поискать по синонимам
            List<Term> aliases = getAllAliases(term);
            List<String> aliasesSearchQueries = getAllMorphs(aliases);
            foundItems.addAll(searchDao.searchInDb(searchQueries, skipResults, PAGE_SIZE - foundItems.size(), fromItemNumber));
            searchQueries.addAll(aliasesSearchQueries);
        } else {
            // 4. Поиск фразы (не термин)
            foundItems = searchDao.searchInDb(query, skipResults, PAGE_SIZE, fromItemNumber);
        }

        page.setHasMore(false);

        // 5. Обработка найденных пунктов
        List<Quote> quotes = handleItems.createQuotes(foundItems, searchQueries);
        page.setQuotes(quotes);

        // 6. Сохранение в кеше
        cache.put(cacheKey, page);
        // 7. Вернуть результат
        return page;
    }

    private List<String> getAllMorphs(Term term) {
        return getAllMorphs(asList(term));
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

    private SearchResultPage getCache(String query, Integer page, String fromItemNumber) {
        throw new NotImplementedException();
    }

    private boolean hasCached(String query, Integer page, String fromItemNumber) {
        throw new NotImplementedException();
    }

    private String prepareQuery(String query) {
        throw new NotImplementedException();
    }

}
