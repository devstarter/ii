package org.ayfaar.app.utils.search;

import org.ayfaar.app.model.Item;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import org.ayfaar.app.controllers.NewSearchController.Quote;

import java.util.List;

//Обработка найденных пунктов
public class HandleItems {
    private List<Item> foundedItems;
    private String requiredPhrase;

    public HandleItems(List<Item> items, String phrase) {
        foundedItems = items;
        requiredPhrase = phrase;
    }

    public List<Quote> createQuotes(List<Item> foundedItems) {
        throw new NotImplementedException();
    }

    public List<Quote> changeSentenceWithRequiredPhrase(List<Quote> quotes) {
        // пройтись по всем пунктам и вырезать предложением, в котором встречаеться поисковая фраза или фразы
        // Если до или после найденной фразы слов больше чем 10, то обрезать всё до (или после) 10 слова и поставить "..."
        throw new NotImplementedException();
    }

    public String decorateRequiredPhrase(String sentence) {
        // Обозначить поисковую фразу или фразы тегами <strong></strong>
        throw new NotImplementedException();
    }
}
