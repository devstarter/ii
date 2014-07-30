package org.ayfaar.app.controllers.search;

import org.ayfaar.app.controllers.search.SearchQuotesHelper;
import org.ayfaar.app.controllers.search.Quote;
import org.ayfaar.app.model.Item;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchQuotesHelperUnitTest {

    private SearchQuotesHelper handleItems;
    private List<Item> items;
    private List<String> phrases;

    @Before
    public void init() {
        handleItems = new SearchQuotesHelper();
        items = new ArrayList<Item>();
        phrases = new ArrayList<String>();

        String content1 = "В инерционной последовательности всех Фокусов Форм Самосознаний это происходит непрерывно и вечно," +
                        " в то время как в ноовременном (то есть не зависящем от влияний фактора Времени) режиме ЭТО уже изначально " +
                        "осуществилось в Одно Единое Мгновение Вечности.";
        String content2 = "Принцип Дувуйллерртности - свойство очень схожих и близких по степени своей качественности энергоинформационных" +
                " структур Энерго-Плазмы резонационно трансформироваться (перефокусироваться, индивидуально «перепроецироваться») в Пространстве-Времени" +
                " в функционально аналогичные системы (то есть непрерывные, описывающиеся функцией Времени), образуя всё бесконечное множество диффузгентно " +
                "(то есть постепенно, сразу во всех возможных направлениях изменения качественности) и последовательно трансмутирующихся друг в друга фокусных " +
                "Конфигураций всевозможных Форм Самосознаний.";
        String content3 = "Так, например, орлы и другие птицы в представлении некоторых горских народов" +
                            " Кавказа являются воплощением «человеческой» души.";
        String content4 = "Почему, например, «люди» или многие другие животные не летают как птицы?";
        String content5 = "Причём в ИИССИИДИОЛОГИИ речь в основном идёт о САМОСОЗНАНИИ и бесчисленных Формах его одновременного проявления в разнокачественных " +
                "энергоинформационных условиях (типах мерности) Пространства-Времени (собственно, сллоогрентный набор всех разнокачественных Конфигураций разнотипных" +
                " Форм Самосознаний - это и есть Пространство-Время).";

        Item item1 = new Item();
        Item item2 = new Item();
        Item item3 = new Item();
        Item item4 = new Item();
        Item item5 = new Item();
        item1.setContent(content1);
        item2.setContent(content2);
        item3.setContent(content3);
        item4.setContent(content4);
        item5.setContent(content5);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);

        phrases.add("время");
        phrases.add("времени");
    }

    @Test
    public void testCreateQuotes() {
        String expected1 = "...Фокусов Форм Самосознаний это происходит непрерывно и вечно," +
                " в то <strong>время</strong> как в ноовременном (то есть не зависящем от влияний фактора <strong>Времени</strong>) режиме ЭТО уже изначально " +
                "осуществилось в Одно Единое Мгновение Вечности.";
        String expected2 = "...структур Энерго-Плазмы резонационно трансформироваться (перефокусироваться, индивидуально «перепроецироваться») в Пространстве-<strong>Времени</strong>" +
                " в функционально аналогичные системы (то есть непрерывные, описывающиеся функцией <strong>Времени</strong>), образуя всё бесконечное множество диффузгентно " +
                "(то есть постепенно, сразу во...";
        String expected3 = "...его одновременного проявления в разнокачественных " +
                "энергоинформационных условиях (типах мерности) Пространства-<strong>Времени</strong> (собственно, сллоогрентный набор всех разнокачественных Конфигураций разнотипных" +
                " Форм Самосознаний - это и есть Пространство-<strong>Время</strong>).";

        List<Quote> actual = handleItems.createQuotes(items, phrases);

        assertTrue(actual.size() == 3);
        assertEquals(expected1, actual.get(0).getQuote());
        assertEquals(expected2, actual.get(1).getQuote());
        assertEquals(expected3, actual.get(2).getQuote());
    }
}
