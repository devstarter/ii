package org.ayfaar.app.controllers.search;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.AbstractTest;
import org.ayfaar.app.model.Item;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.assertEquals;

@Ignore
// todo уберите эту анатацию (@Ignore), она здесь для того чтобв в ветке мастер этот тест не запускался,
// так как он валится, то есть ваша задача зделать так чтобы он успешно выполнялся
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

    @Test
    public void testDividedIntoSentence(){
        List<String> expectedSentence0131 = asList("В свою очередь, разнообразное проявление всех этих эффектов " +
                        "возможно только потому, что, наряду с другими Космическими Законами и Их Принципами (сллоогрентности, " +
                        "дувуйллерртности, резонационности, скррууллерртности и так далее), в Энерго-Плазме активно проявлен " +
                        "и Принцип ротационности.",
                "Этот Принцип, благодаря потенциальным свойствам резонационности любой из Конфигураций, сллоогрентно " +
                        "структурирующих все Уровни мерности Энерго-Плазмы, позволяет Формо-Творцам разнокачественных " +
                        "Форм Самосознаний «помгновенно» фокусироваться в наиболее соответствующих их Конфигурациям " +
                        "«участках» всеобщей сллоогрентности (то есть резонационно «схлопывать временные петли» между " +
                        "схожими «участками» Конфигураций), тем самым порождая в информационном пространстве " +
                        "Самосознаний эффект субъективного (очень узкого, ограниченного) восприятия «самих себя» в " +
                        "неких специфических условиях психоментального проявления, «плотноплазменные» варианты которых " +
                        "вы определяете как «физическое» Пространство-Время.");
        List<String> expectedSentence0846 = asList("Сам этот факт означает для нас то, что на этом - самом-самом что " +
                        "ни на есть «начальном» - «этапе» Акта Проявления всего множества р-Конфигураций разнородных " +
                        "фрагментов Информации из «статуса» «Всё-Что-Есть» в «статус» «Всё Сущее», из-за пока ещё отсутствия " +
                        "«внутренней» Динамики между менее коварллертными Фокусами, образовавшееся статично-гармоничное " +
                        "Состояние Энерго-Информации оказывается неспособным отражать собой какие-то признаки потенциального " +
                        "наличия в нём временной функции, хотя Квинтэссенция Абсолютного Времени УЖЕ в самом «Начале» " +
                        "зарождения Энерго-Плазмы потенциально присутствует в Ней!",
                "Где же или в чём же Она - эта Квинтэссенция Времени - сокрыта?",
                "Предвидя ваш вопрос: «Откуда в Мироздании появился фактор Времени?», - я отвечаю на него: из самого " +
                        "Принципа проявления Энерго-Плазмы, который как раз и обеспечивается той бесконечной " +
                        "разнородностью индивидуальных признаков, что уже «изначально» присуща всей Информации!",
                "Расшифровываю: каждая отличительная особенность коварллертно сочетающихся между собой реконверстных " +
                        "Конфигураций представляет собой некий потенциальный вектор возможной их качественной разницы " +
                        "со множеством других дувуйллерртных признаков, одновременно с этим свойственных остальным " +
                        "разнородным фрагментарным сочетаниям.");



    }

    @Test
    public void testGetQuoteFromSentences(){
        throw new NotImplementedException();
    }

    @Test
    public void testGetNewQuoteByItem(){
        throw new NotImplementedException();
    }

    @Test
    public void testContainSearchQueries(){
        throw new NotImplementedException();
    }

    @Test
    public void testGetDecorateQuote(){
        throw new NotImplementedException();
    }

    @Test
    public void testAddStrong(){
        throw new NotImplementedException();
    }

    @Test
    public void testCutOffWord(){
        throw new NotImplementedException();
    }
}
