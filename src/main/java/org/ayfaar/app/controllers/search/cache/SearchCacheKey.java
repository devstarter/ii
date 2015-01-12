package org.ayfaar.app.controllers.search.cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class SearchCacheKey {
    public final String query;
    public final Integer page;
}
