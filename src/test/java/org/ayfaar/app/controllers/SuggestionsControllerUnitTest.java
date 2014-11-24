package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.NewAliasesMap;
import org.ayfaar.app.utils.TermsMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ayfaar.app.utils.TermsMap.TermProvider;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SuggestionsControllerUnitTest {
    @Mock TermsMap termsMap;
    @Mock NewAliasesMap aliasesMap;

    @InjectMocks
    SuggestionsController controller;

    @Test
    public void testSequence() {
        String q = "a";
        List<String> fakeTerms = asList("a", "1 a", "bterfd", "b-aaaa", "aa", "242a424");

        Mockito.when(termsMap.getAll()).thenReturn(transform(fakeTerms));

        List<String> suggestions = controller.suggestions(q);

        assertEquals(5, suggestions.size());
        assertEquals("a", suggestions.get(0));
        assertEquals("aa", suggestions.get(1));
        assertEquals("1 a", suggestions.get(2));
        assertEquals("b-aaaa", suggestions.get(3));
        assertEquals("242a424", suggestions.get(4));
    }

    @Test
    public void testMaxSize() {
        String q = "гал";
        List<String> fakeTerms = asList("Галактическая Сущность", "Галактическая Странник", "Галактический УЛУУГУМА-Дезинтеграционный Луч АИЙ-ЙЯ",
                                        "Звёздно-Галактическая Форма", "Вселенский Межгалактический Диапазон", "Межгалактический",
                                        "Межгалактические Комплекс-Планы", "Межгалактический Астральный Комплекс-План", "Галактика");

        Mockito.when(termsMap.getAll()).thenReturn(transform(fakeTerms));

        List<String> suggestions = controller.suggestions(q);
        assertTrue(suggestions.size() <= SuggestionsController.MAX_SUGGESTIONS);
    }

    /**
     * Сначала должны быть самые короткие слова (фразы)
     */
    @Test
    public void testOrder() {
        String q = "aa";
        List<String> fakeTerms = asList("aabbcc", "242aa424", "aa", "aabb", "aab");

        Mockito.when(termsMap.getAll()).thenReturn(transform(fakeTerms));

        List<String> suggestions = controller.suggestions(q);

        assertEquals(5, suggestions.size());
        assertEquals("aa", suggestions.get(0));
        assertEquals("aab", suggestions.get(1));
        assertEquals("aabb", suggestions.get(2));
        assertEquals("aabbcc", suggestions.get(3));
        assertEquals("242aa424", suggestions.get(4));
    }

    private List<Map.Entry<String, TermProvider>> transform(List<String> fakeTerms) {
        Map<String, TermsMap.TermProvider> map = new HashMap<String, TermsMap.TermProvider>();
        for(int i = 0; i < fakeTerms.size(); i++) {
            map.put(fakeTerms.get(i), aliasesMap.new TermProviderImpl(fakeTerms.get(i), null, false));
        }
        return new ArrayList<Map.Entry<String, TermProvider>>(map.entrySet());
    }
}
