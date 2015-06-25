package org.ayfaar.app.events;

import org.ayfaar.app.controllers.search.cache.SearchCacheKey;

public class SearchEvent extends LinkPushEvent {
    public SearchEvent(SearchCacheKey key) {
        super("Поиск " + key.query + (key.page > 0 ? " (страница "+(key.page+1)+")" : ""), key.query);
    }

}