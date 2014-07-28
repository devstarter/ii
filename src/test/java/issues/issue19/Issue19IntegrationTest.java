package issues.issue19;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.SuggestionsController;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Issue19IntegrationTest extends IntegrationTest {
    @Inject
    SuggestionsController searchController;

    @Test
    public void test1() {
        String query = "гал";
        List<String> suggestions = searchController.suggestions(query);
        assertEquals(7, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) == 0); // Галактическая Сущность
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) == 0); // Галактическая Странник
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) == 0); // Галактический УЛУУГУМА-Дезинтеграционный Луч АИЙ-ЙЯ
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) > 0); // Звёздно-Галактическая Форма
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) > 0); // Вселенский Межгалактический Диапазон
        assertTrue(suggestions.get(5).toLowerCase().indexOf(query) > 0); // Межгалактические Комплекс-Планы
        assertTrue(suggestions.get(6).toLowerCase().indexOf(query) > 0); // Межгалактический Астральный Комплекс-План
    }

    /**
     * тест на последовательность, то есть упоминания в начале слова, но в середине фразы или следующие после "-"
     * имеют втоорой приоритет
     */
    @Test
    public void test2() {
        String query = "ссс";
        List<String> suggestions = searchController.suggestions(query);
        assertEquals(7, suggestions.size());
        assertEquals("АИЙС-ССС", suggestions.get(5));
        assertEquals("Амициссимное ССС-Состояние", suggestions.get(6));
    }
}
