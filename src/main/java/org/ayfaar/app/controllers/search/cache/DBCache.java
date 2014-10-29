package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

import static org.ayfaar.app.controllers.search.cache.CacheKeyGenerator.SearchCacheKey;

@Component
public class DBCache extends ConcurrentMapCache {
    public static final String CACHE_NAME = "searchResult.json";

    @Inject CustomObjectMapper objectMapper;
    @Inject AliasesMap aliasesMap;

    public DBCache() {
        super(CACHE_NAME);
    }

    @Override
    public ValueWrapper get(Object key) {
        if (key instanceof SearchCacheKey) {
            final Term term = aliasesMap.getTerm(((SearchCacheKey) key).query);
            if (term != null) {
                // ищем в БД кеш по термину
            } else {
                // ищем по key.query
            }

            if (/* если кеш найден */false) {
                put(key, /* найденое значение */1 );
                return new SimpleValueWrapper(/* найденое значение */1 );
            }
        }
        return super.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        String json;
        try {
            json = objectMapper.writeValueAsString(value);
            // todo запись объекта SearchResultPage приобразованого в json в БД, в отдельном потоке, чтоб не ждать результата
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.put(key, json);
    }
}
