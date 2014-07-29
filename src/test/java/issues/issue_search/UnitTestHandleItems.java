package issues.issue_search;

import org.ayfaar.app.controllers.search.HandleItems;

import org.ayfaar.app.controllers.search.Quote;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UnitTestHandleItems {

    private HandleItems handleItems;

    private int sentenceMaxWords = 21;
    private List<Quote> quotes;
    private String phrase;

    @Before
    public void init() {
        quotes = new ArrayList<Quote>();
        String content1 = "Взять хотя бы то, что все ныне принятые Представления о мерностных свойствах Пространства-Времени" +
                " весьма, весьма ограничены и очень далеки от более истинного Понимания Природы подобных явлений.";
        String content2 = "Наука последнего тысячелетия использовала два метода познания - теоретический и экспериментальный," +
                " в то время как интуитивное познание выполняло лишь роль туманных догадок и зыбких предположений, хотя без его" +
                " активного и чаще всего неосознаваемого присутствия в любом творческом процессе ни одно научное открытие не было" +
                " бы возможно.";
        String content3 = "Так, например, орлы и другие птицы в представлении некоторых горских народов" +
                            " Кавказа являются воплощением «человеческой» души.";
        String content4 = "Почему, например, «люди» или многие другие животные не летают как птицы?";
        String content5 = ") существовании всевозможных вариантов (интерпретаций) как воспринимаемых, так и недоступных " +
                "вашему субъективному Восприятию Форм и событий сразу во всём множестве «точек» их разнокачественного" +
                " проявления в Пространстве-Времени.";

        Quote quote1 = new Quote();
        Quote quote2 = new Quote();
        Quote quote3 = new Quote();
        Quote quote4 = new Quote();
        Quote quote5 = new Quote();
        quote1.setQuote(content1);
        quote2.setQuote(content2);
        quote3.setQuote(content3);
        quote4.setQuote(content4);
        quote5.setQuote(content5);
        quotes.add(quote1);
        quotes.add(quote2);
        quotes.add(quote3);
        quotes.add(quote4);
        quotes.add(quote5);

        phrase = "время";
        handleItems = new HandleItems();
    }

    @Test
    public void testChangeSentenceWithRequiredPhraseSize() {
        assertTrue(handleItems.changeSentenceWithRequiredPhrase(quotes).size() == 3);
    }

    @Test
    public void testEqualityChangeSentenceWithRequiredPhrase() {
        String expected = "...бы то, что все ныне принятые Представления о мерностных свойствах Пространства-Времени" +
                " весьма, весьма ограничены и очень далеки от более истинного Понимания...";
        Quote quote = handleItems.changeSentenceWithRequiredPhrase(quotes).get(0);

        assertTrue(quote.getQuote().split(" ").length <= sentenceMaxWords);
        assertEquals(expected, quote.getQuote());
    }

    @Test
    public void testDecorateRequiredPhrase() {
        String sentence = "...бы то, что все ныне принятые Представления о мерностных свойствах Пространства-Времени" +
                " весьма, весьма ограничены и очень далеки от более истинного Понимания...";
        String expected = "...бы то, что все ныне принятые Представления о мерностных свойствах Пространства-<strong>Времени</strong>"
                + " весьма, весьма ограничены и очень далеки от более истинного Понимания...";

        assertEquals(expected, handleItems.decorateRequiredPhrase(sentence));
    }
}
