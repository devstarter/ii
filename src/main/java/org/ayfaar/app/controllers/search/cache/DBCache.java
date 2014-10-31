package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

import static org.ayfaar.app.controllers.search.cache.CacheKeyGenerator.SearchCacheKey;
import static org.ayfaar.app.controllers.search.cache.CacheKeyGenerator.ContentsCacheKey;

@Component
public class DBCache extends ConcurrentMapCache {
    @Inject CustomObjectMapper objectMapper;
    @Inject AliasesMap aliasesMap;
    @Inject CommonDao commonDao;
    @Inject CategoryDao categoryDao;

    public DBCache() {
        super("DBCache");
    }


    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = super.get(key);
        if(value != null) {
            return value;
        }

        JsonEntity jsonEntity = null;
        if (key instanceof SearchCacheKey) {
            final Term term = aliasesMap.getTerm(((SearchCacheKey) key).query);
            if (term != null) {
                jsonEntity = commonDao.get(JsonEntity.class, "uid", term);
            }
        } else if(key instanceof ContentsCacheKey) {
            final Category category = categoryDao.get("uri",
                    UriGenerator.generate(Category.class, ((ContentsCacheKey) key).categoryName));
            if(category != null) {
                jsonEntity = commonDao.get(JsonEntity.class, "uid", category);
            }
        }

        if (jsonEntity != null) {
            put(key, jsonEntity.getJsonContent());
            value = new SimpleValueWrapper(jsonEntity.getJsonContent());
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        String json;
        String name = "";
        UID uid = null;

        if(value instanceof String) {
            json = (String)value;
        }
        else {
            try {
                json = objectMapper.writeValueAsString(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (key instanceof SearchCacheKey) {
            name = ((SearchCacheKey) key).query;
            uid = aliasesMap.getTerm(name);
        } else if(key instanceof ContentsCacheKey) {
            name = ((ContentsCacheKey) key).categoryName;
            uid = commonDao.get(UID.class, UriGenerator.generate(Category.class, name));
        }
        if(uid != null) {
            commonDao.save(new JsonEntity(name, uid, json));
        }
        super.put(key, json);
    }
}
