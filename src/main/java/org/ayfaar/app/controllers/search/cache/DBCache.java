package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.events.SearchEvent;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;


@Component
public class DBCache extends ConcurrentMapCache {
    @Inject CustomObjectMapper objectMapper;
    @Inject TermsMap termsMap;
    @Inject CommonDao commonDao;
    @Inject CategoryDao categoryDao;
    @Inject ApplicationEventPublisher eventPublisher;

    public DBCache() {
        super("DBCache");
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = super.get(key);
        if(value != null) {
            return value;
        }

        CacheEntity cacheEntity = null;
        if (key instanceof SearchCacheKey) {
            SearchCacheKey searchKey = (SearchCacheKey) key;
            boolean isTerm = false;
            if (searchKey.page == 0) {
                final TermsMap.TermProvider provider = termsMap.getTermProvider(searchKey.query);
                String termUri = null;
                if (provider != null) {
                    termUri = provider.hasMainTerm() ? provider.getMainTermProvider().getUri() : provider.getUri();
                }

                if (termUri != null) {
                    cacheEntity = commonDao.get(CacheEntity.class, termUri);
                    isTerm = true;
                }
            }
            eventPublisher.publishEvent(new SearchEvent(searchKey, isTerm));

        } else if(key instanceof ContentsCacheKey) {
            final Category category = categoryDao.get("uri",
                    UriGenerator.generate(Category.class, ((ContentsCacheKey) key).categoryName));
            if(category != null) {
                cacheEntity = commonDao.get(CacheEntity.class, "uri", category.getUri());
            }
        }

        if (cacheEntity != null) {
            put(key, cacheEntity.getJsonContent());
            value = new SimpleValueWrapper(cacheEntity.getJsonContent());
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

        if (key instanceof SearchCacheKey && ((SearchCacheKey) key).page == 0) {
            TermsMap.TermProvider provider = termsMap.getTermProvider(((SearchCacheKey) key).query);
            if(provider != null && ((SearchCacheKey) key).page == 0) {
                uid = provider.hasMainTerm() ? provider.getMainTermProvider().getTerm() : provider.getTerm();
            }
        } else if(key instanceof ContentsCacheKey) {
            String name = ((ContentsCacheKey) key).categoryName;
            uid = commonDao.get(UID.class, UriGenerator.generate(Category.class, name));
        }
        if(uid != null) {
            commonDao.save(new CacheEntity(uid, json));
        }
        super.put(key, json);
    }

    public void clearAll(){
        super.clear();
    }

    public void clearByURI(URI uri){
        super.evict(uri.toString());
    }

}
