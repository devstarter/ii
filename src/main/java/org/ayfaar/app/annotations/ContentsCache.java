package org.ayfaar.app.annotations;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
@Cacheable(value = "DBCache", key = "new org.ayfaar.app.controllers.search.cache.ContentsCacheKey(#name)")
public @interface ContentsCache {
}
