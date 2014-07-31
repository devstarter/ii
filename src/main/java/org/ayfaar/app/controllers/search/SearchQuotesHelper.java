package org.ayfaar.app.controllers.search;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.model.Item;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//5.Обработка найденных пунктов
@Component
public class SearchQuotesHelper {

    public static final int MAX_WORDS_ON_BOUNDARIES = 30;

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        // пройтись по всем пунктам и вырезать предложением, в котором встречаеться поисковая фраза или фразы
        // Если до или после найденной фразы слов больше чем 30 (MAX_WORDS_ON_BOUNDARIES),
        // то обрезать всё до (или после) 30 слова и поставить "..."
        // Обозначить поисковую фразу или фразы тегами <strong></strong>

        List<String> allSentences;
        Quote currentQuote;
        String stringQuote;
        List<Quote> result = new ArrayList<Quote>();
        for(Item item:foundedItems) {
            // 1. Разбиваем абзац на предложения
            allSentences = dividedIntoSentence(item.getContent());
            // 2. Фильтруем и декореруем предложения
            stringQuote = getQuoteFromSentences(allSentences,allPossibleSearchQueries);

            // 3. Создаем объект Quote на основе Item, заполнаем цитату и добавляем в результат
            currentQuote = getNewQuoteByItem(item);
            currentQuote.setQuote(stringQuote);
            result.add(currentQuote);
        }

        return result;
    }

    List<String> dividedIntoSentence(String content){
        throw new NotImplementedException();
    }

    boolean containSearchQueries(String sentence, List<String> allSearchQueries){
        throw new NotImplementedException();
    }

    String addStrongTeg(String sentences, List<String> allSearchQueries){
        throw new NotImplementedException();
    }

    String cutOffWord(String sentences,List<String> allSearchQueries){
        throw new NotImplementedException();
    }

    private String getQuoteFromSentences(List<String> allSentences, List<String> allSearchQueries){
        StringBuffer quote = new StringBuffer();
        for (String sentence:allSentences){
            // 2.1 Проверяем есть ли в данном предложении ключеевые фразы
            if(containSearchQueries(sentence,allSearchQueries)){
                quote.append("\n");
                // 2.2  Декорируем предложение и добавляем крезультирующей строке
                quote.append(getDecorateQuote(sentence,allSearchQueries));
            }
        }
        return quote.substring(2).toString();
    }

    private Quote getNewQuoteByItem(Item item){
        Quote quote = new Quote();
        quote.setUri(item.getUri());
        return quote;
    }

    private String getDecorateQuote(String sentences,List<String> allSearchQueries){
        // 2.2.1 Обрезаем слова до или после найденной фразы если их больше чем (MAX_WORDS_ON_BOUNDARIES)
        String result = cutOffWord(sentences,allSearchQueries);
        // 2.2.2 Обральяем ключевые фразы тегами <strong></strong>
        return addStrongTeg(result, allSearchQueries);
    }
}
