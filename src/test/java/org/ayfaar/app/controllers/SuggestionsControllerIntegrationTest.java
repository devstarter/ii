package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SuggestionsControllerIntegrationTest extends IntegrationTest {
    @Inject
    SuggestionsController searchController;

    @Test
    public void test1() {
        String query = "гал";
        List<String> suggestions = searchController.suggestions(query);

        assertEquals(7, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) > 0); // ПИНГАЛА
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) > 0); // СВААГАЛИ
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) == 0); // Галактическая Сущность
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) == 0); // Галактический Странник
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) > 0); // Звёздно-Галактическая Форма
        assertTrue(suggestions.get(5).toLowerCase().indexOf(query) > 0); // Межгалактические Комплекс-Планы
        assertTrue(suggestions.get(6).toLowerCase().indexOf(query) == 0); // Галактический УЛУУГУМА-Дезинтеграционный Луч АИЙ-ЙЯ
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
        assertEquals("СССВ-УУИЙ-СССВ", suggestions.get(5));
        assertEquals("ССС-ЮИЙЙ-ЙЙ-ССС", suggestions.get(6));
    }

    @Test
    public void testLowerUpperCase() {
        List<String> suggestionsUpper = searchController.suggestions("НН");
        List<String> suggestionsLower = searchController.suggestions("нн");

        assertEquals(suggestionsLower.size(), suggestionsUpper.size());

        for (int i = 0; i < suggestionsLower.size() && suggestionsLower.size()==suggestionsUpper.size(); i++) {
            assertEquals(suggestionsLower.get(i), suggestionsUpper.get(i));
        }
    }

    @Test
    public void testSuggestionsWhenQueryContainsDot() {
        assertEquals(0, searchController.suggestions("2.0001").size());
    }

    @Test
    public void testSuggestionsWhenQueryContainsBracket() {
        assertEquals(0, searchController.suggestions("унго-ссвооун)").size());
    }

    @Test
    public void testHyphenWords(){
        String query = "слосна";
        List<String> suggestions = searchController.suggestions(query);
        assertEquals("ССЛОО-СС-СНАА",suggestions.get(0));
//        query = "насм";
//        suggestions = searchController.suggestions(query);

    }
}
