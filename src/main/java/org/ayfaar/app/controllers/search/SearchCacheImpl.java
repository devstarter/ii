package org.ayfaar.app.controllers.search;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dim on 09.08.2014.
 */
public class SearchCacheImpl implements SearchCache{
    private Map<Object, SearchResultPage> library;

    public SearchCacheImpl() {
        library = new HashMap<Object, SearchResultPage>();
    }

    @Override
    public Object generateKey(String query, Integer pageNumber, String fromItemNumber){
        if (fromItemNumber == null) return query + pageNumber.toString() + "null";
        return query + pageNumber.toString() + fromItemNumber;
    }

    @Override
    public boolean has(Object cacheKey) {
        return library.containsKey(cacheKey) && library.get(cacheKey) != null;
    }

    @Override
    public SearchResultPage get(Object cacheKey){
        return library.get(cacheKey);
    }

    @Override
    public void put(Object cacheKey, SearchResultPage page){
        if (cacheKey == null) throw new IllegalArgumentException();
        if (page == null) throw new IllegalArgumentException();
        library.put(cacheKey, page);
    }
}
