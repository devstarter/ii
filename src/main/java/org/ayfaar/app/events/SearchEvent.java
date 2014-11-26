package org.ayfaar.app.events;

import org.ayfaar.app.controllers.search.cache.SearchCacheKey;

public class SearchEvent extends SimplePushEvent {
    public SearchEvent(SearchCacheKey key, boolean isTerm) {
        super("Поиск "
                + (!isTerm ? "не термина " : "")
                + key.query
                + (key.page > 0 ? " страница "+key.page+1 : ""));
    }
}
