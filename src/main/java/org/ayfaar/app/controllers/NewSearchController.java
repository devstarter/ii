package org.ayfaar.app.controllers;

import org.ayfaar.app.annotations.SearchResultCache;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.controllers.search.cache.DBCache;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.TermsMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.utils.TermsMap.TermProvider;
import static java.util.Arrays.asList;

@Controller
@RequestMapping("api/v2/search")
public class NewSearchController {
    public static final int PAGE_SIZE = 20;
    @Inject
    private SearchQuotesHelper handleItems;

    @Inject
    private SearchDao searchDao;

    @Inject
    private TermsMap termsMap;

    @Inject
    protected DBCache cache;

    /**
     * Поиск будет производить только по содержимому Item
     *
     * @param pageNumber номер страницы
     */
    @SearchResultCache
    @RequestMapping
    @ResponseBody
    // возвращаем Object чтобы можно было вернуть закешированный json или SearchResultPage
    public Object search(@RequestParam String query,
                                   @RequestParam Integer pageNumber,
                                   @RequestParam(required = false) String fromItemNumber) {
        // 1. Очищаем введённую фразу от лишних пробелов по краям и переводим в нижний регистр
        query = prepareQuery(query);

        SearchResultPage page = new SearchResultPage();
        page.setHasMore(false);

        // 3. Определить термин ли это
        TermProvider provider = termsMap.getTermProvider(query);
        // 3.1. Если да, Получить все синониме термина
        List<Item> foundItems;
        // указывает сколько результатов поиска нужно пропустить, то есть когда ищем следующую страницу
        int skipResults = pageNumber*PAGE_SIZE;

        List<String> searchQueries;
        if (provider != null) {
            // 3.2. Получить все падежи по всем терминам
            searchQueries = provider.getMorphs();
            // 4. Произвести поиск
            // 4.1. Сначала поискать совпадение термина в различных падежах
            foundItems = searchDao.findInItems(searchQueries, skipResults, PAGE_SIZE + 1, fromItemNumber);
            if (foundItems.size() < PAGE_SIZE) {
                // 4.2. Если количества не достаточно для заполнения страницы то поискать по синонимам
                List<TermProvider> aliases = getAllAliases(provider);
                // Если у термина вообще есть синонимы:
                if (!aliases.isEmpty()) {
                    List<String> aliasesSearchQueries = getAllMorphs(aliases);
                    foundItems.addAll(searchDao.findInItems(aliasesSearchQueries, skipResults,
                            PAGE_SIZE - foundItems.size() + 1, fromItemNumber));
                    searchQueries.addAll(aliasesSearchQueries);
                }
            }
        } else {
            // 4. Поиск фразы (не термин)
            searchQueries = asList(query.replace("*", "%"));
            foundItems = searchDao.findInItems(searchQueries, skipResults, PAGE_SIZE + 1, fromItemNumber);
            searchQueries = asList(query.replace("%", ""));
        }

        if (foundItems.size() > PAGE_SIZE ) {
            foundItems.remove(foundItems.size() - 1);
            page.setHasMore(true);
        }

        // 5. Обработка найденных пунктов
        List<Quote> quotes = handleItems.createQuotes(foundItems, searchQueries);
        page.setQuotes(quotes);

        // 7. Вернуть результат
        return page;
    }

    List<String> getAllMorphs(List<TermProvider> providers) {
        List<String> morphs = new ArrayList<String>();

        for(TermProvider provider : providers) {
            morphs.addAll(provider.getMorphs());
        }
        return morphs;
    }

    List<TermProvider> getAllAliases(TermProvider provider) {
        List<TermProvider> aliases = new ArrayList<TermProvider>();
        TermProvider code = provider.getCode();

        aliases.addAll(provider.getAliases());
        aliases.addAll(provider.getAbbreviations());
        if(code != null) {
            aliases.add(provider.getCode());
        }
        return aliases;
    }

    private String prepareQuery(String query) {
        // 1. Очищаем введённую фразу от лишних пробелов по краям и переводим в нижний регистр
        return query != null ? query.toLowerCase().trim() : null;
    }

    @RequestMapping("clean")
    public void cleanCache() {
        cache.clear();
    }

    public Object searchWithoutCache(String query, Integer pageNumber, String fromItemNumber) {
        return search(query, pageNumber, fromItemNumber);
    }
}
