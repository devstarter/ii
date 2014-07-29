package org.ayfaar.app.utils.search;

import lombok.Data;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import org.ayfaar.app.controllers.NewSearchController.Quote;

import java.util.ArrayList;
import java.util.List;

//Обработка найденных пунктов
@Data
public class HandleItems {
    @Autowired
    private NewSearchController controller;

    private List<Item> foundedItems;
    private String requiredPhrase;

    public List<Quote> createQuotes(List<Item> foundedItems) {
        //throw new NotImplementedException();

        List<Quote> quotes = new ArrayList<Quote>();
        for(Item i : foundedItems) {
            Quote quote = controller.new Quote();
            quote.setUri(i.getUri());
            quote.setQuote(i.getContent());
            quotes.add(quote);
        }
        quotes = changeSentenceWithRequiredPhrase(quotes);

        for(int i = 0; i < quotes.size(); i++) {
            Quote quote = controller.new Quote();
            quote.setQuote(decorateRequiredPhrase(quotes.get(i).getQuote()));
            quotes.set(i, quote);
        }
        return quotes;
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
