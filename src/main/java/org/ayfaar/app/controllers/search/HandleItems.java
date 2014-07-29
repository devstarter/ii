package org.ayfaar.app.controllers.search;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

//5.Обработка найденных пунктов
@Component
public class HandleItems {
    @Autowired
    private NewSearchController controller;

    public static final int sentenceMaxWords = 30;

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        throw new NotImplementedException();
    }
}
