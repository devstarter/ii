package org.ayfaar.app.controllers.search.cache;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ayfaar.app.controllers.CategoryController;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.events.SimplePushEvent;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

public class CacheUpdater {
    @Autowired
    private NewSearchController searchController;
    @Autowired
    private CategoryController categoryController;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private TermsMap termsMap;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Inject
    CustomObjectMapper objectMapper;

//    @Scheduled(cron="0 0 19 * * ?") // это 3 по Москве, так как время сервера в EST, таблица соответствия http://www.worldtimebuddy.com/?qm=1&lid=5,703448,524901&h=5&date=2014-12-28&sln=19-20
    public void update() throws IOException {
        long start = System.currentTimeMillis();

        termsMap.reload();
        updateCacheSearchResult();

        long end = System.currentTimeMillis();
        final String duration = DurationFormatUtils.formatDuration(end - start, "HH:mm:ss");
        eventPublisher.publishEvent(new SimplePushEvent("Кеш обновлён за "+duration));
    }

    private void updateCacheSearchResult() throws IOException {
        //clean cache for search results

        String uri = UriGenerator.generate(Term.class, "");
        List<CacheEntity> searchCacheList = commonDao.getLike(CacheEntity.class, "uri", uri + "%", Integer.MAX_VALUE);

        for (CacheEntity cache : searchCacheList) {
            final SearchResultPage searchResult = (SearchResultPage) searchController.searchWithoutCache(
                    getValueFromUri(Term.class, cache.getUri()), 0, null);
            cache.setContent(objectMapper.writeValueAsString(searchResult));
            commonDao.save(cache);
        }
    }

    public void update(String uri) {
        //clean cache by uri

        if(uri.startsWith(UriGenerator.generate(Term.class, ""))) {
            searchController.search(getValueFromUri(Term.class, uri), 0, null);
        } else if(uri.startsWith(UriGenerator.generate(Category.class, ""))) {
            categoryController.getContents(getValueFromUri(Category.class, uri));
        }
    }
}
