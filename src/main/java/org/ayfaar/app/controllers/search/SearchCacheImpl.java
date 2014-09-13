package org.ayfaar.app.controllers.search;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SearchCacheImpl implements SearchCache{
    private Map<Object, SearchResultPage> cache;

    public SearchCacheImpl() {
        cache = new HashMap<Object, SearchResultPage>();
    }

    @Override
    public Object generateKey(String query, Integer pageNumber, String fromItemNumber) throws NullPointerException{
        if (fromItemNumber != null) {
            return query + pageNumber + fromItemNumber;
        } else {
            return query + pageNumber + "null";
        }
    }

    @Override
    public boolean has(Object cacheKey) {
        return cache.containsKey(cacheKey) && cache.get(cacheKey) != null;
    }

    @Override
    public SearchResultPage get(Object cacheKey){
        return cache.get(cacheKey);
    }

    @Override
    public void put(Object cacheKey, SearchResultPage page){
        if (cacheKey == null) throw new IllegalArgumentException();
        if (page == null) throw new IllegalArgumentException();
        cache.put(cacheKey, page);
    }
}
