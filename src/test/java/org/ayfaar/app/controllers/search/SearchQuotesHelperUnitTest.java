package org.ayfaar.app.controllers.search;

import org.ayfaar.app.AbstractTest;
import org.ayfaar.app.model.Item;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.assertEquals;

public class SearchQuotesHelperUnitTest extends AbstractTest {

    private final List<Item> items;
    private SearchQuotesHelper handleItems;
    private List<String> queries;

    public SearchQuotesHelperUnitTest() throws IOException {
        Item item_1_0846 = new Item("1.0846", getFile("item-1.0846.txt"));
        Item item_1_0131 = new Item("1.0131", getFile("item-1.0131.txt"));
        items = unmodifiableList(asList(item_1_0131, item_1_0846));
    }

    @Before
    public void init() throws IOException {
        handleItems = new SearchQuotesHelper();
        queries = asList("время", "Времени", "Временем", "Временах", "Временами"); // и тп...
    }

    @Test
    public void testCreateQuotes() {
        String expectedUri1 = "ии:пункт:1.0131";
        String expectedQuote1 = "...«участками» Конфигураций), тем самым порождая в информационном пространстве " +
                "Самосознаний эффект субъективного (очень узкого, ограниченного) восприятия «самих себя» в неких " +
                "специфических условиях психоментального проявления, «плотноплазменные» варианты которых вы определяете" +
                " как «физическое» Пространство-<strong>Время</strong>.";

        String expectedUri2 = "ии:пункт:1.0846";
        String expectedQuote2 = "Взять хотя бы то, что все ныне принятые Представления о мерностных свойствах " +
                "Пространства-<strong>Времени</strong> весьма, весьма ограничены и очень далеки от более истинного " +
                "Понимания Природы подобных явлений.";

        List<Quote> actual = handleItems.createQuotes(items, queries);

        assertEquals(2, actual.size());
        assertEquals(expectedUri1, actual.get(0).getUri());
        assertEquals(expectedQuote1, actual.get(0).getQuote());

        assertEquals(expectedUri2, actual.get(1).getQuote());
        assertEquals(expectedQuote2, actual.get(1).getQuote());
    }
}
