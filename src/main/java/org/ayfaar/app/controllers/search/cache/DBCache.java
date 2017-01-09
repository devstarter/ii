package org.ayfaar.app.controllers.search.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;


@Component
public class DBCache extends ConcurrentMapCache {
    @Inject ObjectMapper objectMapper;
    @Inject TermService termService;
    @Inject CommonDao commonDao;
    @Inject CategoryDao categoryDao;

    public boolean disabled;

    public DBCache() {
        super("DBCache");
    }


    @Override
    public ValueWrapper get(Object key) {
        if (disabled) return super.get(key);

        ValueWrapper value = super.get(key);
        if(value != null) {
            return value;
        }

        CacheEntity cacheEntity = null;
        if (key instanceof SearchCacheKey) {
            SearchCacheKey searchKey = (SearchCacheKey) key;
            boolean isTerm = false;
            if (searchKey.page == 0 && (searchKey.startFrom == null || searchKey.startFrom.isEmpty())) {
                final Optional<TermService.TermProvider> providerOpt = termService.getMainOrThis(searchKey.query);
                String termUri = null;
                if (providerOpt.isPresent()) {
                    termUri = providerOpt.get().getUri();
                }
                if (termUri != null) {
                    cacheEntity = commonDao.get(CacheEntity.class, termUri);
                    isTerm = true;
                }
            }
            if (!isTerm && searchKey.query.indexOf("Обсуждение:") != 0 && searchKey.query.indexOf("_") != 0) {
//                eventPublisher.publishEvent(new SearchEvent(searchKey));
            }

        } else if(key instanceof ContentsCacheKey) {
            final Category category = categoryDao.get("uri",
                    UriGenerator.generate(Category.class, ((ContentsCacheKey) key).categoryName));
            if(category != null) {
                cacheEntity = commonDao.get(CacheEntity.class, "uri", category.getUri());
            }
        }

        if (cacheEntity != null) {
            put(key, cacheEntity.getContent());
            value = new SimpleValueWrapper(cacheEntity.getContent());
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        if (disabled) return;

        String json;
        UID uid = null;

        try {
            json = (value instanceof String) ? (String)value : objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (key instanceof SearchCacheKey && ((SearchCacheKey) key).page == 0) {
            Optional<TermService.TermProvider> providerOpt = termService.getMainOrThis(((SearchCacheKey) key).query);
            if(providerOpt.isPresent() && ((SearchCacheKey) key).page == 0) {
                uid = providerOpt.get().getTerm();
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
}
