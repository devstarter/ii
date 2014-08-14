package org.ayfaar.app.controllers.search;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SearchCacheImpl implements SearchCache {
    private Map<Object, SearchResultPage> myCache;

    public SearchCacheImpl() {
        myCache = new HashMap<Object, SearchResultPage>();
    }

    @Override
    public Object generateKey(String query, Integer pageNumber, String fromItemNumber) throws NullPointerException{
        String cacheKey = "";
        try {
            cacheKey = query + pageNumber + fromItemNumber;
            return cacheKey;
        }catch (NullPointerException e){
            cacheKey = query + pageNumber + "null";
            return cacheKey;
        }
    }

    @Override
    public boolean has(Object cacheKey) {
        return cacheKey != null;
    }

    @Override
    public SearchResultPage get(Object cacheKey) {
        return myCache.get(cacheKey);
    }

    @Override
    public void put(Object cacheKey, SearchResultPage page) throws IllegalArgumentException{
        try {
            myCache.put(cacheKey, page);
        }catch(IllegalArgumentException e){}
    }
}
