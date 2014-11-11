package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;


@Component
public class DBCache extends ConcurrentMapCache {
    @Inject CustomObjectMapper objectMapper;
    @Inject TermsMap termsMap;
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
            final TermsMap.TermProvider provider = termsMap.getTermProvider(((SearchCacheKey) key).query);
            String termUri = null;
            if(provider != null) {
                termUri = provider.hasMainTerm() ? provider.getMainTermProvider().getUri() : provider.getUri();
            }

            if (termUri != null) {
                jsonEntity = commonDao.get(JsonEntity.class, termUri);
            }
            else {
                //todo создать уведомление о том, что ищут не термин
            }
        } else if(key instanceof ContentsCacheKey) {
            final Category category = categoryDao.get("uri",
                    UriGenerator.generate(Category.class, ((ContentsCacheKey) key).categoryName));
            if(category != null) {
                jsonEntity = commonDao.get(JsonEntity.class, "uri", category.getUri());
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
        UID uid = null;

        try {
            json = (value instanceof String) ? (String)value : objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (key instanceof SearchCacheKey) {
            TermsMap.TermProvider provider = termsMap.getTermProvider(((SearchCacheKey) key).query);
            if(provider != null && ((SearchCacheKey) key).page == 1) {
                uid = provider.hasMainTerm() ? provider.getMainTermProvider().getTerm() : provider.getTerm();
            }
        } else if(key instanceof ContentsCacheKey) {
            String name = ((ContentsCacheKey) key).categoryName;
            uid = commonDao.get(UID.class, UriGenerator.generate(Category.class, name));
        }
        if(uid != null) {
            commonDao.save(new JsonEntity(uid, json));
        }
        super.put(key, json);
    }
}
