package org.ayfaar.app.controllers.search.cache;


import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;


public class DBCacheManager extends AbstractCacheManager {
    public static final String SEARCH_CACHE_NAME = "searchResult.json";

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return asList(new DbCache(SEARCH_CACHE_NAME, new ConcurrentHashMap<Object, Object>(), false));
    }

}
