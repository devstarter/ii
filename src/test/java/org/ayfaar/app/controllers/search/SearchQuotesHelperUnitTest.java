package org.ayfaar.app.controllers.search;

import org.ayfaar.app.AbstractTest;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.UriGenerator;
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
        Item item_1_0131 = new Item("1.0131", getFile("item-1.0131.txt"));
        Item item_1_0771 = new Item("1.0771", getFile("item-1.0771.txt"));
        Item item_1_0846 = new Item("1.0846", getFile("item-1.0846.txt"));
        item_1_0131.setUri(UriGenerator.generate(item_1_0131));
        item_1_0771.setUri(UriGenerator.generate(item_1_0771));
        item_1_0846.setUri(UriGenerator.generate(item_1_0846));

        items = unmodifiableList(asList(item_1_0131, item_1_0771, item_1_0846));
    }

    @Before
    public void init() throws Exception {
        SearchQuotesHelper.MAX_WORDS_ON_BOUNDARIES = 30;
        handleItems = new SearchQuotesHelper();
//        final Field max_words_on_boundaries = handleItems.getClass().getField("MAX_WORDS_ON_BOUNDARIES");
//        ReflectionUtils.setFinalStatic(handleItems, max_words_on_boundaries, 30);
        queries = asList("время", "Времени", "Временем", "Временах", "Временами");
    }

    @Test
    public void testCreatePartQuoteIfLeftPartContainsBracket() {
        String content = "Всю. Информацию, копируемую.) с ЛЛААСС-Форм, ГЛООГОЛМ-ГЛЛИИ-Творцы (специфически?) перекодируют и адаптируют в виде двух эфирных Потоков, один из которых " +
                "содержит только «проекции» первичных кодировок данного Вселенского Творения, и наполняют Их <strong>";
        String expectedQuote = " Информацию, копируемую.) с ЛЛААСС-Форм, ГЛООГОЛМ-ГЛЛИИ-Творцы (специфически?) перекодируют и адаптируют в виде двух эфирных Потоков, один из которых " +
                "содержит только «проекции» первичных кодировок данного Вселенского Творения, и наполняют Их <strong>";

        String actualQuote = handleItems.getPartQuote(content, "([\\.\\?!]*)([^\\.\\?!]*)(<strong>)", "", "left");
        assertEquals(expectedQuote, actualQuote);
    }

    @Test
    public void testCreatePartQuoteIfRightPartContainsBracket() {
        String content = "<strong>Содержанием</strong> все Формо-структуры (Стабилизационного?) План-Обертона (которые никогда не изменяются, служа для Формо-Творцов как бы своеобразными " +
                "Эталонами Творения!), в то время.) как НИИССЛИИ-И-Творцы Трансмутационного План-Обертона – через Формо-структуры Ментального, Астрального и Каузального " +
                "План-Обертонов - дублируют и свилгс-сферационно трансгрессируют все. эти «проекции» «низших» Уровней (с ±14,0-±13,0 мерности) Вторичной Иллюзии в " +
                "Фокусную Динамику синтезирующих Формо-Творцов Третичной Иллюзии.";
        String expectedQuote = "<strong>Содержанием</strong> все Формо-структуры (Стабилизационного?) План-Обертона (которые никогда не изменяются, служа для Формо-Творцов как" +
                " бы своеобразными Эталонами Творения!), в то время.) как НИИССЛИИ-И-Творцы Трансмутационного План-Обертона – через Формо-структуры Ментального, Астрального и " +
                "Каузального План-Обертонов - дублируют и свилгс-сферационно трансгрессируют все.";

        String actualQuote = handleItems.getPartQuote(content, "(<strong>)([^\\.\\?!]*)([\\.\\?!]*)", "", "right");
        assertEquals(expectedQuote, actualQuote);
    }

    @Test
    public void testCreateQuotesWhenPhraseStartsWithRequiredQuery() throws IOException {
        String expectedQuote = "<strong>Время</strong> характеризует собой два момента - начало взаимодействия с позиции данной диссонационности между двумя Фокусами и его окончание," +
                " когда эти два диссонационных по отношению друг к другу Фокуса смогут.";
        Item item_first_word = new Item("1.0771", getFile("firstWord.txt"));
        List<Quote> actual = handleItems.createQuotes(asList(item_first_word), queries);

        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void testCreateQuotesWhenPhraseEndsWithRequiredQuery() throws IOException {
        String expectedQuote = "Эти Уровни мерности стали основой для всевозможных проявлений <strong>Времени</strong>.";
        Item item_last_word = new Item("1.0770", getFile("lastWord.txt"));
        List<Quote> actual = handleItems.createQuotes(asList(item_last_word), queries);

        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void testCreateQuotesWhenBeforeRequiredPhraseIsHyphen() throws IOException {
        String expectedQuote = "Вибрационная Матрица (определённая часть сллоогрентного Смысла, энергоинформационной Сути) каждого" +
                " Звукового Космического Кода, правильно воспроизведённая вами при помощи речевого аппарата, через Фокусную" +
                " Динамику Самосознания мгновенно трансформирует качественную динамику Формо-структур окружающего вас" +
                " Пространства-<strong>Времени</strong> в состояния, соответствующие по частоте Матрицам «Мысленного" +
                " Кода-анализатора» и «Плазменного Кода-излучателя» («мысленный» - потому что отражает наши субъективные " +
                "Представления о чём бы то ни было, адаптированные к Схеме Синтеза людей,...";

        Item item_more = new Item("1.0622", getFile("beforeRequiredPhraseIsHyphen.txt"));
        List<Quote> actual = handleItems.createQuotes(asList(item_more), queries);

        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void testCreateQuotesWithFewQueriesWhenPhraseHasLessThanThirtyWords() throws IOException {
        String expectedQuote = "Для этого есть огромное количество способов, начиная с технических (каким, например, в моём компьютере во <strong>время</strong> работы было мгновенно и автоматически" +
                " «вставлено» одним непрерывным файлом, а затем так же автоматически разделено с помощью «бегающего» курсора более 150 тысяч Тоновых Имён, адаптированных к нашим системам" +
                " Восприятия) и заканчивая индивидуальными, когда заинтересованный человек во <strong>время</strong> глубоких осознанных Медитаций узнаёт Свой Звуковой Космический Код," +
                " отражающий гораздо более качественные Состояния Самосознания.";

        Item item_less = new Item("1.0014", getFile("lessThanThirtyWords.txt"));
        List<Quote> actual = handleItems.createQuotes(asList(item_less), queries);

        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void testCreateQuotesWithFewQueriesWhenPhraseHasMoreThanThirtyWords() throws IOException {
        String expectedQuote = "...аналогов Форм Самосознаний скорость перехода диссипативного состояния Энергии в реализационное (декогерентное) значительно превышает возможности обратного" +
                " процесса, что как бы очень сильно «растягивает» прежнее динамичное состояние проявления данного колебания (волны) во <strong>времени</strong>, настолько сильно уменьшая её " +
                "ёмкостные энергоинформационные параметры (тем самым увеличивая «длину» волны – расстояние между максимальными «всплесками резонационного взаимодействия» качественных проявлений" +
                " Формо-Творцов, которые характерны для осуществления обратной связи – перехода...";

        Item item_more = new Item("1.0622", getFile("moreThanThirtyWords.txt"));
        List<Quote> actual = handleItems.createQuotes(asList(item_more), queries);

        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void testCreateQuotesWithPunctuationAndBracket() throws IOException {
        String expectedQuote = "Всю Информацию, копируемую с ЛЛААСС-Форм, ГЛООГОЛМ-ГЛЛИИ-Творцы специфически перекодируют " +
                "и адаптируют в виде двух эфирных Потоков, один из которых содержит только «проекции» первичных кодировок данного" +
                " Вселенского Творения, и наполняют Их <strong>Содержанием</strong> все Формо-структуры Стабилизационного " +
                "План-Обертона (которые никогда не изменяются, служа для Формо-Творцов как бы своеобразными Эталонами Творения!), " +
                "в то время как НИИССЛИИ-И-Творцы Трансмутационного План-Обертона – через Формо-структуры Ментального, Астрального" +
                " и Каузального...";

        Item item_3_1225 = new Item("3.1225", getFile("item-3.1225.txt"));
        List<Item> item = unmodifiableList(asList(item_3_1225));
        List<Quote> actual = handleItems.createQuotes(item, asList("Содержанием"));

        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void testCreateQuotesWhenFewItems() throws IOException {
        String expectedUri1 = "1.0131";
        String expectedQuote1 = "...«участками» Конфигураций), тем самым порождая в информационном пространстве Самосознаний эффект субъективного (очень узкого, ограниченного) восприятия " +
                "«самих себя» в неких специфических условиях психоментального проявления, «плотноплазменные» варианты которых вы определяете как «физическое» Пространство-<strong>Время</strong>.";
        String expectedUri2 = "1.0771";
        String expectedQuote2 = "<strong>Время</strong> характеризует собой два момента - начало взаимодействия с позиции данной диссонационности между двумя Фокусами и его окончание," +
                " когда эти два диссонационных по отношению друг к другу Фокуса смогут образовать единую резонационную фокусную Конфигурацию. <strong>Время</strong> не сокращает " +
                "существующее диссонационное расстояние, оно только показывает, сколько ещё разнородной Информации (в виде Фокусов соответствующей Ей Энергии) необходимо вложить в данное " +
                "фокусное взаимодействие, чтобы аннигилировать данный тип диссонанса, «перепроецировав» Фокусную Динамику Формо-Творцов в более качественный Уровень возможных взаимосвязей." +
                " Чем более качественная (то есть структурированная большим количеством уже готовых коварллертных взаимосвязей) Энергия в этом участвует, тем меньше <strong>Времени</strong> " +
                "требуется на аннигиляцию тензорности, которая характерна лишь для данных условий проявления данной Формы Самосознания. Именно качественность используемой ею Информации влияет" +
                " на результат изменения реализационных возможностей Формо-Творцов данной Формы, стимулируя процесс «перепроецирования» их Фокусной Динамики в более соответствующую этой" +
                " Информации Форму Самосознания. Это похоже на навигатор дорожных маршрутов: когда вы отмечаете какую-то точку на карте навигатора, то он сразу же показывает, сколько " +
                "<strong>времени</strong> понадобится вам, чтобы прибыть в выбранное вами место, двигаясь с определённой скоростью.";
        String expectedUri3 = "1.0846";
        String expectedQuote3 = "...Сущее», из-за пока ещё отсутствия «внутренней» Динамики между менее коварллертными Фокусами, образовавшееся статично-гармоничное Состояние Энерго-Информации" +
                " оказывается неспособным отражать собой какие-то признаки потенциального наличия в нём временной функции, хотя Квинтэссенция Абсолютного <strong>Времени</strong> УЖЕ в самом" +
                " «Начале» зарождения Энерго-Плазмы потенциально присутствует в Ней! Где же или в чём же Она - эта Квинтэссенция <strong>Времени</strong> - сокрыта? Предвидя ваш вопрос: «Откуда" +
                " в Мироздании появился фактор <strong>Времени</strong>?», - я отвечаю на него: из самого Принципа проявления Энерго-Плазмы, который как раз и обеспечивается той бесконечной " +
                "разнородностью индивидуальных признаков, что уже «изначально» присуща всей Информации!";
        List<Quote> actual = handleItems.createQuotes(items, queries);

        assertEquals(expectedUri1, actual.get(0).getNumber());
        assertEquals(expectedQuote1, actual.get(0).getQuote());
        assertEquals(expectedUri2, actual.get(1).getNumber());
        assertEquals(expectedQuote2, actual.get(1).getQuote());
        assertEquals(expectedUri3, actual.get(2).getNumber());
        assertEquals(expectedQuote3, actual.get(2).getQuote());
    }

    @Test
    public void testGetPartQuoteWhenQuoteStartsWithBracket() throws IOException {
        String expectedQuote = "3) — диапазон 2-3-й мерностей: КУ-У-ВВУ-Дооллсы — ЛУ-У-ВВУ, ВУ-У-ВВУ, ФУ-У-ВВУ, " +
                "РУ-У-ВВУ,ГУ-У-ВВУ, КК-У-ВВУ, ЛЛ-У-ВВУ, ВВ-У-ВВУ, ФФ-У-ВВУ,РР-У-ВВУ, ГГ-У-ВВУ, КА-А-ВВУ, ЛА-А-ВВУ, " +
                "ВА-А-ВВУ,ФА-А-ВВУ, <strong>РА</strong>-А-ВВУ, ГА-А-ВВУ, КК-А-ВВУ, ЛЛ-А-ВВУ,ВВ-А-ВВУ, ФФ-А-ВВУ, " +
                "РР-А-ВВУ, ГТ-А-ВВУ...";

        Item item = new Item("10.11834", getFile("item-10.11834.txt"));
        List<Quote> actual = handleItems.createQuotes(asList(item), asList("РА"));

        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void spacer() throws IOException {
        Item item = new Item("1.0744", getFile("item-1.0744.txt"));
        final String actual = handleItems.createQuotes(asList(item), asList("ра", "резонационной Активностью")).get(0).getQuote();

        String expected = "В результате осуществления такой взаимосвязи образовалось некое парное сочетание " +
                "очень схожей между собой Информации; условно назовём это " +
                "состояние «<strong>резонационной Активностью</strong>» (<strong>РА</strong>).";
        assertEquals(expected, actual);
    }

    @Test
    public void test() throws IOException {
        Item item = new Item("1.0463", getFile("item-1.0463.txt"));
        String expectedQuote = "Другими словами, <strong>декогерентность</strong> означает, что в каждый конкретный" +
                " миг вы способны сделать лишь тот – единственный! – выбор, который уже «изначально» определён теми" +
                " узкоспецифическими взаимосвязями, которые в этот момент структурируют фокусируемую вами" +
                " НУУ-ВВУ-Конфигурацию.";

        List<Quote> actual = handleItems.createQuotes(asList(item), asList("декогерентность"));
        assertEquals(expectedQuote, actual.get(0).getQuote());
    }

    @Test
    public void testGetPartRightWhenHyphen() {
        String content = "<strong>Содержанием</strong> все Формо-структуры " +
                "Стабилизационного План-Обертона! - (которые никогда не изменяются, служа для Формо-Творцов как бы " +
                "своеобразными Эталонами Творения!), в то время как НИИССЛИИ-И-Творцы Трансмутационного План-Обертона " +
                "– через Формо-структуры Ментального, Астрального и Каузального План-Обертонов - дублируют и " +
                "свилгс-сферационно трансгрессируют все эти «проекции» «низших» Уровней (с ±14,0-±13,0 мерности) " +
                "Вторичной Иллюзии в Фокусную Динамику синтезирующих Формо-Творцов Третичной Иллюзии.";
        String expectedQuote = "<strong>Содержанием</strong> все Формо-структуры " +
                "Стабилизационного План-Обертона! - (которые никогда не изменяются, служа для Формо-Творцов как бы " +
                "своеобразными Эталонами Творения!), в то время как НИИССЛИИ-И-Творцы Трансмутационного План-Обертона " +
                "– через Формо-структуры Ментального, Астрального и Каузального План-Обертонов - дублируют и " +
                "свилгс-сферационно трансгрессируют все эти «проекции» «низших» Уровней (с ±14,0-±13,0 мерности) " +
                "Вторичной Иллюзии в Фокусную Динамику синтезирующих Формо-Творцов Третичной Иллюзии.";

        String actualQuote = handleItems.getPartQuote(content, "(<strong>)([^\\.\\?!]*)([\\.\\?!]*)", "", "right");
        assertEquals(expectedQuote, actualQuote);
    }

    @Test
    public void testGetPartLeftWhenHyphen() {
        String content = "который уже «изначально» определён. Другими словами, означает, что в каждый. - конкретный" +
                " миг вы способны?) сделать лишь тот – единственный! - выбор, который уже «изначально» определён" +
                " теми узкоспецифическими взаимосвязями, <strong>";
        String expectedQuote = " Другими словами, означает, что в каждый. - конкретный миг вы " +
                "способны?) сделать лишь тот – единственный! - выбор, который уже «изначально» определён теми" +
                " узкоспецифическими взаимосвязями, <strong>";

        String actualQuote = handleItems.getPartQuote(content, "([\\.\\?!]*)([^\\.\\?!]*)(<strong>)", "", "left");
        assertEquals(expectedQuote, actualQuote);
    }
}
