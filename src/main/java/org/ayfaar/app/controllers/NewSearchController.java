package org.ayfaar.app.controllers;

import net.sf.cglib.core.Transformer;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchCache;
import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.dao.TermMorphDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.ayfaar.app.utils.AliasesMap;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static net.sf.cglib.core.CollectionUtils.transform;

//todo пометить как контролер и зделать доступнім по адресу "v2/search"
public class NewSearchController {
    public static final int PAGE_SIZE = 20;
    @Inject
    private SearchQuotesHelper handleItems;

    @Inject
    private SearchDao searchDao;

    @Inject
    private AliasesMap aliasesMap;

    @Inject
    private TermMorphDao termMorphDao;

    @Inject
    private LinkDao linkDao;

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
        page.setHasMore(false);

        // 3. Определить термин ли это
        Term term = aliasesMap.getTerm(query);
        // 3.1. Если да, Получить все синониме термина
        List<Item> foundItems;
        // указывает сколько результатов поиска нужно пропустиьб, то есть когда ищем следующую страницу
        int skipResults = pageNumber*PAGE_SIZE;

        if (term != null) {
            // 3.2. Получить все падежи по всем терминам
            searchQueries = getAllMorphs(term);
            // 4. Произвести поиск
            // 4.1. Сначала поискать совпадение термина в различных падежах
            foundItems = searchDao.findInItems(searchQueries, skipResults, PAGE_SIZE + 1, fromItemNumber);
            if (foundItems.size() < PAGE_SIZE) {
                // 4.2. Если количества не достаточно для заполнения страницы то поискать по синонимам
                List<Term> aliases = getAllAliases(term);
                // Если у термина вообще есть синонимы:
                if (!aliases.isEmpty()) {
                    List<String> aliasesSearchQueries = getAllMorphs(aliases);
                    searchQueries.addAll(aliasesSearchQueries);
                    foundItems.addAll(searchDao.findInItems(searchQueries, skipResults,
                            PAGE_SIZE - foundItems.size() + 1, fromItemNumber));
                    //searchQueries.addAll(aliasesSearchQueries);
                }
            }
        } else {
            // 4. Поиск фразы (не термин)
            foundItems = searchDao.findInItems(asList(query), skipResults, PAGE_SIZE + 1, fromItemNumber);
        }

        if (foundItems.size() > PAGE_SIZE ) {
            foundItems.remove(foundItems.size() - 1);
            page.setHasMore(true);
        }


        // 5. Обработка найденных пунктов
        List<Quote> quotes = handleItems.createQuotes(foundItems, searchQueries);
        page.setQuotes(quotes);

        // 6. Сохранение в кеше
        cache.put(cacheKey, page);
        // 7. Вернуть результат
        return page;
    }

    List<String> getAllMorphs(Term term) {
        return getAllMorphs(asList(term));
    }

    // todo добавить тесты для этого метода
    public List<String> getAllMorphs(List<Term> terms) {
        List<String> allWordsModes = new ArrayList<String>();
        List<TermMorph> morphs = new ArrayList<TermMorph>();
        for (Term term : terms) {
            morphs.addAll(termMorphDao.getList("termUri", term.getUri()));
            allWordsModes.add(term.getName());
        }
        //noinspection unchecked
        allWordsModes.addAll(transform(morphs, new Transformer() {
            @Override
            public Object transform(Object value) {
                return ((TermMorph) value).getName();
            }
        }));
        return allWordsModes;
    }

    List<Term> getAllAliases(Term term) {
        List<Term> aliases = new ArrayList<Term>();
        for (Link link : linkDao.getAliases(term.getUri())) {
            aliases.add((Term) link.getUid2());
        }
        return aliases;
    }

    private String prepareQuery(String query) {
        // 1. Очищаем введённую фразу от лишних пробелов по краям и переводим в нижний регистр
        return query != null ? query.toLowerCase().trim() : null;
    }

}
