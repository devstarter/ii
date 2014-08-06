package org.ayfaar.app.controllers.search;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.model.Item;
import org.springframework.stereotype.Component;

import java.util.List;

//5.Обработка найденных пунктов
@Component
public class SearchQuotesHelper {

    public static final int MAX_WORDS_ON_BOUNDARIES = 30;

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        // пройтись по всем пунктам и вырезать предложением, в котором встречаеться поисковая фраза или фразы
        // Если до или после найденной фразы слов больше чем 30 (MAX_WORDS_ON_BOUNDARIES),
        // то обрезать всё до (или после) 30 слова и поставить "..."
        // Обозначить поисковую фразу или фразы тегами <strong>фраза</strong>
        throw new NotImplementedException();
    }
}
