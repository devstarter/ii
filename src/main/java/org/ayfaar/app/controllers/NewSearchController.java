package org.ayfaar.app.controllers;

import lombok.Data;
import org.ayfaar.app.annotations.SearchResultCache;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.controllers.search.cache.DBCache;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.ContentsService.ContentsProvider;
import org.ayfaar.app.utils.RegExpUtils;
import org.ayfaar.app.utils.StringUtils;
import org.ayfaar.app.utils.TermService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.ayfaar.app.utils.TermService.TermProvider;

@Controller
@RequestMapping("api/v2/search")
public class NewSearchController {
    public static final int PAGE_SIZE = 20;
    @Inject SearchQuotesHelper handleItems;
    @Inject SearchDao searchDao;
    @Inject TermService termService;
    @Inject
    ContentsService contentsService;
    @Inject DBCache cache;
//    @Inject ApplicationEventPublisher eventPublisher;
//    @Inject CacheUpdater cacheUpdater;

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
                         @RequestParam(required = false) String startFrom) {
        // 1. Очищаем введённую фразу от лишних пробелов по краям и переводим в нижний регистр
        query = prepareQuery(query);

        SearchResultPage page = new SearchResultPage();
        page.setHasMore(false);

        // 3. Определить термин ли это
        Optional<TermProvider> providerOpt = termService.get(query);
        // 3.1. Если да, Получить все синониме термина
        List<Item> foundItems;
        // указывает сколько результатов поиска нужно пропустить, то есть когда ищем следующую страницу
        int skipResults = pageNumber*PAGE_SIZE;

        List<String> searchQueries;
        if (providerOpt.isPresent()) {
            // 3.2. Получить все падежи по всем терминам
            searchQueries = providerOpt.get().getAllAliasesWithAllMorphs();
            // 4. Произвести поиск
            foundItems = searchDao.findInItems(searchQueries, skipResults, PAGE_SIZE + 1, startFrom);

//            if (foundItems.isEmpty()) {
//                eventPublisher.publishEvent(new LinkPushEvent("Не найдено - "+provider.getName(), provider.getName()));
//            }
        } else {
            // 4. Поиск фразы (не термин)
            query = query.replace("!", "");
            searchQueries = asList(query.replace("%", "\\%").replace("*", "%"));
            foundItems = searchDao.findInItems(searchQueries, skipResults, PAGE_SIZE + 1, startFrom);
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

    @RequestMapping("categories")
    @ResponseBody
    public Object inCategories(@RequestParam String query) {
        final Optional<TermProvider> providerOpt = termService.get(query);
        List<String> searchQueries;
        if (providerOpt.isPresent()) {
            TermProvider provider = providerOpt.get();
            provider = provider.getMainTerm().orElse(provider);
            searchQueries = provider.getAllAliasesAndAbbreviationsWithAllMorphs();
        } else {
			query = query.replace("*", RegExpUtils.w+"+");
            searchQueries = Collections.singletonList(query);
        }
		List<? extends ContentsProvider> foundCategoryProviders = contentsService.descriptionContains(searchQueries);

		List<FoundCategoryPresentation> presentations = new ArrayList<>();
		for (ContentsService.ContentsProvider p : foundCategoryProviders) {
			String strongMarkedDescription = StringUtils.markWithStrong(p.description(), searchQueries);
			FoundCategoryPresentation presentation = new FoundCategoryPresentation(p.path(), p.uri(), strongMarkedDescription);
			presentations.add(presentation);
		}

		return presentations;
    }

    private String prepareQuery(String query) {
        if (query != null) {
            query = query.replace("Обсуждение:", "");
            query = query.replace("_", " ");
            query = query.toLowerCase().trim();
        }
        return  query;
    }

    @RequestMapping("cache/clean")
    public void cleanCache() {
        cache.clear();
    }

    @RequestMapping("cache/update")
    public void updateCache() throws IOException {
//        cacheUpdater.update();
    }

    public Object searchWithoutCache(String query, Integer pageNumber, String fromItemNumber) {
        return search(query, pageNumber, fromItemNumber);
    }

	@Data
	private class FoundCategoryPresentation {
		private final String path;
		private final String uri;
		private final String description;

		public FoundCategoryPresentation(String path, String uri, String description) {
			this.path = path;
			this.uri = uri;
			this.description = description;
		}
	}
}
