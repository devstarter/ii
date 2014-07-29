package org.ayfaar.app.controllers.search;

import org.ayfaar.app.model.Item;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

//Обработка найденных пунктов
@Component
public class HandleItems {

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
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
