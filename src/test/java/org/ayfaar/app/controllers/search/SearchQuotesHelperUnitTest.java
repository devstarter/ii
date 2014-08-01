package org.ayfaar.app.controllers.search;

import org.ayfaar.app.AbstractTest;
import org.ayfaar.app.model.Item;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
// todo уберите эту анатацию (@Ignore), она здесь для того чтобв в ветке мастер этот тест не запускался,
// так как он валится, то есть ваша задача зделать так чтобы он успешно выполнялся
public class SearchQuotesHelperUnitTest extends AbstractTest {

    private final List<Item> items;
    private final Item item_1_0846;
    private final Item item_1_0131;
    private SearchQuotesHelper handleItems;
    private List<String> queries;

    private <T> void assertListEquals(List<T> expected, List<T> actual){
        if(expected == null || actual == null) {
            assertEquals(expected,actual);
            return;
        }

        //fixme: порядок элементов массива может быть разный
        assertEquals(expected.size(),actual.size());
        if(expected.size() == actual.size()){
            for(int i = 0; i < actual.size();i++)
                assertTrue(expected.contains(actual.get(i)));
        }
    }

    public SearchQuotesHelperUnitTest() throws IOException {
        item_1_0846 = new Item("1.0846", getFile("item-1.0846.txt"));
        item_1_0131 = new Item("1.0131", getFile("item-1.0131.txt"));
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

    @Test
    public void testDividedIntoSentence() throws IOException {
        List<String> expectedSentence0131 = asList(getFile("item-1.0131_bySentence.txt").split("\n"));
        List<String> expectedSentence0846 = asList(getFile("item-1.0846_bySentence.txt").split("\n"));
        // todo добавить пункт 3.1180, там есть "..Плазме!), чем..", то есть знак окончания предложения внутри скобок

        List<String> sentenceList_1_0131 = handleItems.dividedIntoSentence(item_1_0131.getContent());
        //fixme для однозначности теста лучше сравнивать с конкретным айтемом чем получать его из списка, в предыдущей строке пример исправления
        List<String> sentenceList_1_0846 = handleItems.dividedIntoSentence(items.get(1).getContent());

        assertListEquals(expectedSentence0131,sentenceList_1_0131);
        assertListEquals(expectedSentence0846,sentenceList_1_0846);
     }

    @Test
    public void testContainSearchQueries(){
        Map<Boolean,String> testMap = new HashMap<Boolean,String>();
        testMap.put(true,"остранства-Времени весьма, в");
        testMap.put(true,"остранства-Временах весьма, в");
        testMap.put(true,"остранства-Временами весьма, в");
        testMap.put(true,"время остранства-Времени весьма, в");
        testMap.put(false,"остранства-весьма, ввремя");
        testMap.put(false,"остранства-весьма, в");

        for (Map.Entry<Boolean,String> pair:testMap.entrySet()){
            assertEquals(pair.getKey(),handleItems.containSearchQueries(pair.getValue(),queries));
        }
    }

    @Test
    public void testAddStrongTeg(){
        Map<String,String> testMap = new HashMap<String,String>();
        testMap.put("остранства-<strong>Времени</strong> весьма, в","остранства-Времени весьма, в");
        testMap.put("остранства-<strong>Временах</strong> весьма, в","остранства-Временах весьма, в");
        testMap.put("остранства-<strong>Временами</strong> весьма, в","остранства-Временами весьма, в");
        testMap.put("<strong>время</strong> остранства-<strong>Времени</strong> весьма, в","время остранства-Времени весьма, в");
        testMap.put("<strong>время</strong> остранства-весьма, вВременами","время остранства-весьма, вВременами");

        String actual;
        for (Map.Entry<String,String> pair:testMap.entrySet()){
            actual = handleItems.addStrongTeg(pair.getValue(),queries);
            assertEquals(pair.getKey(),actual);
        }
    }

    @Test
    public void testCutOffWord(){
        String baseQuote1 = "...«участками» Конфигураций), тем самым порождая в информационном пространстве " +
                "Самосознаний эффект субъективного (очень узкого, ограниченного) восприятия «самих себя» в неких " +
                "специфических условиях психоментального проявления, «плотноплазменные» варианты которых вы определяете" +
                " как «физическое» Пространство-Время.";
        String expectedQuote1 = "...«участками» Конфигураций), тем самым порождая в информационном пространстве " +
                "Самосознаний эффект субъективного (очень узкого, ограниченного) восприятия «самих себя» в неких " +
                "специфических условиях психоментального проявления, «плотноплазменные» варианты которых вы определяете" +
                " как «физическое» Пространство-Время.";
        String expectedQuote2 = "Взять хотя бы то, что все ныне принятые Представления о мерностных свойствах " +
                "Пространства-Времени весьма, весьма ограничены и очень далеки от более истинного " +
                "Понимания Природы подобных явлений.";

        String actualQuote1 = handleItems.cutOffWord(baseQuote1,queries);
        String actualQuote2 = handleItems.cutOffWord(expectedQuote2, queries);

        assertEquals(expectedQuote1,actualQuote1);
        assertEquals(expectedQuote2,actualQuote2);
    }
}
