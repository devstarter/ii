package org.ayfaar.app.controllers.search;


import lombok.Data;
import org.ayfaar.app.controllers.NewSearchController;

import java.util.List;

//@Data
public class Cache {
    private List<Quote> quotes;

    public List<Quote> getCache(String query, Integer pageNumber, SearchFilter filter) {
        int start = pageNumber * NewSearchController.PAGE_SIZE;
        int temp = start + NewSearchController.PAGE_SIZE;
        int end = temp < quotes.size() ? temp : quotes.size();
        return quotes.subList(start, end);
    }
}
