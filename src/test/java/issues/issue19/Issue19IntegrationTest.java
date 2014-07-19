package issues.issue19;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.SearchController2;
import org.ayfaar.app.controllers.search.Suggestion;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Issue19IntegrationTest extends IntegrationTest {
    @Inject SearchController2 searchController;

    @Test
    public void test1() {
        String query = "гал";
        List<Suggestion> suggestions = searchController.suggestions(query);
        assertEquals(7, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toString().toLowerCase().indexOf(query) == 0); // Галактическая Сущность
        assertTrue(suggestions.get(1).toString().toLowerCase().indexOf(query) == 0); // Галактическая Странник
        assertTrue(suggestions.get(2).toString().toLowerCase().indexOf(query) == 0); // Галактический УЛУУГУМА-Дезинтеграционный Луч АИЙ-ЙЯ
        assertTrue(suggestions.get(3).toString().toLowerCase().indexOf(query) > 0); // Звёздно-Галактическая Форма
        assertTrue(suggestions.get(4).toString().toLowerCase().indexOf(query) > 0); // Вселенский Межгалактический Диапазон
        assertTrue(suggestions.get(5).toString().toLowerCase().indexOf(query) > 0); // Межгалактические Комплекс-Планы
        assertTrue(suggestions.get(6).toString().toLowerCase().indexOf(query) > 0); // Межгалактический Астральный Комплекс-План
    }

    /**
     * тест на последовательность, то есть упоминания в начале слова, но в середине фразы или следующие после "-"
     * имеют втоорой приоритет
     * Для проверки:
     SELECT  `name` FROM `ii`.`term` WHERE `name` LIKE 'ссс%'
     UNION
     SELECT  `name` FROM `ii`.`term` WHERE `name` LIKE '% ссс%' or `name` LIKE '%-ссс%'
     UNION
     SELECT  `name` FROM `ii`.`term` WHERE `name` LIKE '%ссс%'

     Но юнионом делать не стоит так как это перегружает базу. Нам нужно только 7 результатов и если они все в начале
     фразы то не нужно делать все следующине запросы, юнион сначале сделает выборку по всем запросом а затем применит
     лимит ко всему результату. Пока значений в базе данных мало то это не столь важно, но со временем нагрузка
     повысится, лучше сразу сделать правильно
     */
    @Test
    public void test2() {
        String query = "ссс";
        List<Suggestion> suggestions = searchController.suggestions(query);
        assertEquals("АИЙС-ССС", suggestions.get(5).toString());
        assertEquals("Амициссимное ССС-Состояние", suggestions.get(6).toString());
    }
}
