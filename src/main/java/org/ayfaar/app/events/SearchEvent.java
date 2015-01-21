package org.ayfaar.app.events;

import org.ayfaar.app.controllers.search.cache.SearchCacheKey;

public class SearchEvent extends LinkPushEvent {
    public SearchEvent(SearchCacheKey key) {
        super();
        title = "Поиск "
                + key.query
                + (key.page > 0 ? " (страница "+(key.page+1)+")" : "");
        url = getUrlToTerm(key.query);
    }

}