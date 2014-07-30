package org.ayfaar.app.controllers.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultPage {
    private List<Quote> quotes;
    private boolean hasMore;
}
