package org.ayfaar.app.controllers.search;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.model.Item;
import org.springframework.stereotype.Component;

import java.util.List;

//5.Обработка найденных пунктов
@Component
public class HandleItems {
    public static final int sentenceMaxWords = 30;

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        throw new NotImplementedException();
    }
}
