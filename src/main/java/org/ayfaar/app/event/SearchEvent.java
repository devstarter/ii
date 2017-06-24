package org.ayfaar.app.event;

import org.ayfaar.app.controllers.search.cache.SearchCacheKey;

public class SearchEvent extends LinkPushEvent {
    public SearchEvent(SearchCacheKey key) {
        super("Поиск " + key.query
                + (key.startFrom != null && !key.startFrom.isEmpty() ? " начиная с "+key.startFrom : "")
                + (key.page > 0 ? " (страница "+(key.page+1)+")" : ""), key.query);
    }

}