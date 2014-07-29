package org.ayfaar.app.utils.search;

import org.ayfaar.app.model.Item;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

//Обработка найденных пунктов
public class HandleItems {
    private List<Item> foundedItems;
    private String requiredPhrase;
    private List<String> sentences;
    private List<String> content;

    public HandleItems(List<Item> items, String phrase) {
        foundedItems = items;
        requiredPhrase = phrase;
        sentences = new ArrayList<String>();
    }

    public List<String> getContent(List<Item> foundedItems) {
        throw new NotImplementedException();
    }

    public List<String> getSentenceWithRequiredPhrase(List<String> content, String phrase) {
        // пройтись по всем пунктам и вырезать предложением, в котором встречаеться поисковая фраза или фразы
        // Если до или после найденной фразы слов больше чем 10, то обрезать всё до (или после) 10 слова и поставить "..."
        throw new NotImplementedException();
    }

    public String decorateRequiredPhrase(String sentence, String phrase) {
        // Обозначить поисковую фразу или фразы тегами <strong></strong>
        throw new NotImplementedException();
    }
}
