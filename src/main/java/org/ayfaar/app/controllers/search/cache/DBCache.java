package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

@Component
public class DBCache extends ConcurrentMapCache {
    public static final String CACHE_NAME = "searchResult.json";

    @Inject CustomObjectMapper objectMapper;
//    @Inject TermM

    public DBCache() {
        super(CACHE_NAME);
    }

    @Override
    public ValueWrapper get(Object key) {
        // todo загрузка кеша из БД, и преобразование json -> объект SearchResultPage
        return super.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        // todo запись объекта SearchResultPage приобразованого в json в БД
        String json;
        try {
            json = objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.put(key, json);
    }
}
