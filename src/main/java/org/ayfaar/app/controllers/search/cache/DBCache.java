package org.ayfaar.app.controllers.search.cache;

import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.concurrent.ConcurrentMap;

public class DbCache extends ConcurrentMapCache {
    public DbCache(String name) {
        super(name);
    }

    public DbCache(String name, boolean allowNullValues) {
        super(name, allowNullValues);
    }

    public DbCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues) {
        super(name, store, allowNullValues);
    }

    @Override
    public ValueWrapper get(Object key) {
        // todo загрузка кеша из БД, и преобразование json -> объект SearchResultPage
        return super.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        // todo запись объекта SearchResultPage приобразованого в json в БД
        super.put(key, value);
    }
}
