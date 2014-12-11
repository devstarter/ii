package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.controllers.CategoryController;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.events.SimplePushEvent;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

    @Scheduled(cron="0 0 3 * * ?")
    public void update() {
        long start = System.currentTimeMillis();

        termsMap.reload();
        updateCacheSearchResult();

        long end = System.currentTimeMillis();

        eventPublisher.publishEvent(new SimplePushEvent("update cache", getUpdatingTime(start, end)));
    }

    private void updateCacheSearchResult() {
        //clean cache for search results

        String uri = UriGenerator.generate(Term.class, "");
        List<CacheEntity> searchResultCache = commonDao.getLike(CacheEntity.class, "uri", (uri + "%"), Integer.MAX_VALUE);

        for (CacheEntity searchResult : searchResultCache) {
            searchController.search(UriGenerator.getValueFromUri(Term.class, searchResult.getUri()), 0, null);
        }
    }

    public void update(String uri) {
        //clean cache by uri

        if(uri.startsWith(UriGenerator.generate(Term.class, ""))) {
            searchController.search(UriGenerator.getValueFromUri(Term.class, uri), 0, null);
        } else if(uri.startsWith(UriGenerator.generate(Category.class, ""))) {
            categoryController.getContents(UriGenerator.getValueFromUri(Category.class, uri));
        }
    }

    private String getUpdatingTime(long start, long end) {
        return String.format("cache updated %d seconds", (end - start)/1000);
    }
}
