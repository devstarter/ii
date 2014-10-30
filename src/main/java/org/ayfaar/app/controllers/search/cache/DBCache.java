package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.controllers.search.SearchResultPage;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.spring.converter.json.CustomObjectMapper;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.contents.CategoryPresentation;
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
    @Inject CommonDao commonDao;
    @Inject CategoryDao categoryDao;

    public DBCache() {
        super(CACHE_NAME);
    }

    @Override
    public ValueWrapper get(Object key) {
        if (key instanceof SearchCacheKey) {
            final Term term = aliasesMap.getTerm(((SearchCacheKey) key).query);

            JsonEntity jsonEntity = null;
            if (term != null) {
                jsonEntity = commonDao.get(JsonEntity.class, "uri", term);
            }
            else {
                final Category category = categoryDao.get("name", ((SearchCacheKey) key).query);
                if(category != null) {
                    jsonEntity = commonDao.get(JsonEntity.class, "uri", category);
                } else {
                    jsonEntity = commonDao.get(JsonEntity.class, "name", ((SearchCacheKey) key).query);
                }
            }

            SearchResultPage page = null;
            CategoryPresentation contents = null;
            if (jsonEntity != null) {
                if(jsonEntity.getUri() instanceof Term) {
                    try {
                        page = objectMapper.readValue(jsonEntity.getJsonContent(), SearchResultPage.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //зачем мы опять добавляем в кэш если там и так есть это значение?
                    put(key, page);
                    return new SimpleValueWrapper(page);
                } else if (jsonEntity.getUri() instanceof Category) {
                    try {
                        contents = objectMapper.readValue(jsonEntity.getJsonContent(), CategoryPresentation.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //зачем мы опять добавляем в кэш если там и так есть это значение?
                    put(key, contents);
                    return new SimpleValueWrapper(contents);
                }
            }
        }
        return super.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        String json;
        String name = "";
        String link = "";
        try {
            json = objectMapper.writeValueAsString(value);

            if (key instanceof SearchCacheKey) {
                name = ((SearchCacheKey) key).query;
            }

            if (value instanceof SearchResultPage) {
                link = UriGenerator.generate(Term.class, name);
            }
            else if(value instanceof CategoryPresentation) {
                link = UriGenerator.generate(Category.class, name);
            }

            UID uri = commonDao.get(UID.class, link);
            final JsonEntity jsonEntity = new JsonEntity(name, uri, json);
            commonDao.save(jsonEntity);

            //если истользую отдельный поток, в базу данных ничего не записывается
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    commonDao.save(jsonEntity);
                }
            }).start();*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.put(key, json);
    }
}
