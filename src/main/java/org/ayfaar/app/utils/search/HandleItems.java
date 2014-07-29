package org.ayfaar.app.utils.search;

import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import org.ayfaar.app.controllers.NewSearchController.Quote;

import java.util.ArrayList;
import java.util.List;

//Обработка найденных пунктов
@Component
public class HandleItems {
    @Autowired
    private NewSearchController controller;

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        throw new NotImplementedException();

    }

    public List<Quote> changeSentenceWithRequiredPhrase(List<Quote> quotes) {
        // пройтись по всем пунктам и вырезать предложением, в котором встречаеться поисковая фраза или фразы
        // Если до или после найденной фразы слов больше чем 10, то обрезать всё до (или после) 10 слова и поставить "..."
        //throw new NotImplementedException();
        List<Quote> list = new ArrayList<Quote>();
        //NewSearchController controller1 = new NewSearchController();
        Quote quote = controller.new Quote();
        quote.setQuote("dlfjgldhfgldhfgld ldkjfg dfgdg");

        list.add(quote);
        list.add(quote);
        list.add(quote);
        return list;
    }

    public String decorateRequiredPhrase(String sentence) {
        // Обозначить поисковую фразу или фразы тегами <strong></strong>
        throw new NotImplementedException();
    }
}
