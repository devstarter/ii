package org.ayfaar.app.controllers.search;

public interface SearchCache {
    Object generateKey(String query, Integer pageNumber, String fromItemNumber);
    boolean has(Object cacheKey);
    SearchResultPage get(Object cacheKey);
    void put(Object cacheKey, SearchResultPage page);
}
