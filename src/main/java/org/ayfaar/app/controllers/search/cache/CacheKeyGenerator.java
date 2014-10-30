package org.ayfaar.app.controllers.search.cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.cache.interceptor.DefaultKeyGenerator;

import java.lang.reflect.Method;

public class CacheKeyGenerator extends DefaultKeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (method.toString().equals("public org.ayfaar.app.controllers.search.SearchResultPage org.ayfaar.app.controllers.NewSearchController.search(java.lang.String,java.lang.Integer,java.lang.String)")) {
            // вернём в качестве ключа поисковую фразу с номером страници
            return new SearchCacheKey((String) params[0], (Integer) params[1]);
        } else if (method.toString().equals("public org.ayfaar.app.utils.contents.CategoryPresentation org.ayfaar.app.controllers.CategoryController.getContents(java.lang.String)")){
            // вернём в качестве ключа имя категории
            return new SearchCacheKey((String) params[0], 0);
        } else {
            return super.generate(target, method, params);
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    public static class SearchCacheKey {
        public final String query;
        public final Integer page;
    }
}
