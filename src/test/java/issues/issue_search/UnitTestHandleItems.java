package issues.issue_search;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.search.HandleItems;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UnitTestHandleItems {
    private HandleItems handleItems;
    private int sentenceMaxWords = 21;
    private List<String> contents;
    private String phrase;

    @Before
    public void init() {
        contents = new ArrayList<String>();
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

        contents.add(content1);
        contents.add(content2);
        contents.add(content3);
        contents.add(content4);
        contents.add(content5);

        phrase = "время";
        handleItems = new HandleItems(new ArrayList<Item>(), phrase);
    }

    @Test
    public void testGetSentenceWithRequiredPhraseSize() {
        assertTrue(handleItems.getSentenceWithRequiredPhrase(contents, phrase).size() == 3);
    }

    @Test
    public void testEqualityGetSentenceWithRequiredPhrase() {
        String expected = "...бы то, что все ныне принятые Представления о мерностных свойствах Пространства-Времени" +
                " весьма, весьма ограничены и очень далеки от более истинного Понимания...";
        String content = handleItems.getSentenceWithRequiredPhrase(contents, phrase).get(0);

        assertTrue(content.split(" ").length <= sentenceMaxWords);
        assertEquals(expected, content);
    }

    @Test
    public void testDecorateRequiredPhrase() {
        String sentence = "...бы то, что все ныне принятые Представления о мерностных свойствах Пространства-Времени" +
                " весьма, весьма ограничены и очень далеки от более истинного Понимания...";
        String expected = "...бы то, что все ныне принятые Представления о мерностных свойствах Пространства-<strong>Времени</strong>"
                + " весьма, весьма ограничены и очень далеки от более истинного Понимания...";

        assertEquals(expected, handleItems.decorateRequiredPhrase(sentence, phrase));
    }
}
