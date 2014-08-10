package org.ayfaar.app.controllers.search;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dim on 09.08.2014.
 */
public class SearchCacheImpl implements SearchCache {
    private Map<Object, SearchResultPage> myCache;

    public SearchCacheImpl() {
        myCache = new HashMap<Object, SearchResultPage>();
    }

    @Override
    public Object generateKey(String query, Integer pageNumber, String fromItemNumber) {
        String cacheKey = "";
        cacheKey = query + pageNumber + fromItemNumber;
        return cacheKey;
    }

    @Override
    public boolean has(Object cacheKey) {
        return !cacheKey.equals(null);
    }

    @Override
    public SearchResultPage get(Object cacheKey) {
        return myCache.get(cacheKey);
    }

    @Override
    public void put(Object cacheKey, SearchResultPage page) {
        myCache.put(cacheKey, page);
    }
}
