package org.ayfaar.app.controllers.search.cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class SearchCacheKey {
    public final String query;
    public final String startFrom;
    public final Integer page;
}
