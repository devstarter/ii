package org.ayfaar.app.controllers.search;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dim on 09.08.2014.
 */
public class SearchCacheImpl implements SearchCache{
    private Map<Object, SearchResultPage> myCache; // назови более осмысленно переменную :)

    public SearchCacheImpl() {
        myCache = new HashMap<Object, SearchResultPage>();
    }

    @Override
    public Object generateKey(String query, Integer pageNumber, String fromItemNumber) throws NullPointerException{
        // зачем тебе здесь переменная?
        String cacheKey = "";
        // вместо try catch в данном случае использовать if, так как try catch более ресурсоёмкая операция
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
//        if (myCache.containsKey(cacheKey) && !myCache.get(cacheKey).equals(null)) return true;
//        return false;
        // можно упростить, так мне кажеться читабельнее
        return myCache.containsKey(cacheKey) && myCache.get(cacheKey) != null;
    }

    @Override
    public SearchResultPage get(Object cacheKey){
        // зачем это условие, если кеша по ключу нет то myCache.get(cacheKey) вернёт null
        if (cacheKey.equals(null)) return null;
        return myCache.get(cacheKey);
    }

    @Override
    public void put(Object cacheKey, SearchResultPage page){
        if (cacheKey == null) throw new IllegalArgumentException();
        if (page == null) throw new IllegalArgumentException();
        myCache.put(cacheKey, page);
    }
}
