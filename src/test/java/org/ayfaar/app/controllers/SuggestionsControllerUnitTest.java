package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.TermServiceImpl;
import org.ayfaar.app.utils.TermService;
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
import static org.ayfaar.app.utils.TermService.TermProvider;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SuggestionsControllerUnitTest {
    @Mock
    TermService termService;
    @Mock
    TermServiceImpl aliasesMap;

    @InjectMocks
    SuggestionsController controller;

    @Test
    public void testSequence() {
        String q = "a";
        List<String> fakeTerms = asList("a", "1 a", "bterfd", "b-aaaa", "aa", "242a424");

        Mockito.when(termService.getAll()).thenReturn(transform(fakeTerms));

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

        Mockito.when(termService.getAll()).thenReturn(transform(fakeTerms));

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

        Mockito.when(termService.getAll()).thenReturn(transform(fakeTerms));

        List<String> suggestions = controller.suggestions(q);

        assertEquals(5, suggestions.size());
        assertEquals("aa", suggestions.get(0));
        assertEquals("aab", suggestions.get(1));
        assertEquals("aabb", suggestions.get(2));
        assertEquals("aabbcc", suggestions.get(3));
        assertEquals("242aa424", suggestions.get(4));
    }

    private List<Map.Entry<String, TermProvider>> transform(List<String> fakeTerms) {
        Map<String, TermService.TermProvider> map = new HashMap<String, TermService.TermProvider>();
        for (String fakeTerm : fakeTerms) {
            map.put(fakeTerm, aliasesMap.new TermProviderImpl(fakeTerm, null, false));
        }
        return new ArrayList<Map.Entry<String, TermProvider>>(map.entrySet());
    }

    @Test
    public void duplicationsTest() {
        final String actual = SuggestionsController.addDuplications("abc efg 123");
        assertEquals("a+b+c+ e+f+g+ 123", actual);
    }
}
