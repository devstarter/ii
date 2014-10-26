package org.ayfaar.app.controllers.search.cache;


import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;


public class DBCacheManager implements CacheManager {
    private Collection<DBCache> caches;

    @Override
    public Cache getCache(String name) {
        return null;
    }

    @Override
    public Collection<String> getCacheNames() {
        return null;
    }

    public void setCaches(Collection<DBCache> caches) {
        this.caches = caches;
    }
}
