package org.ayfaar.app.utils;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Term;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class IntegrationTestNewAliasesMap extends IntegrationTest{
    @Autowired
    private NewAliasesMap aliasesMap;
    @Autowired
    private TermsMap termsMap;

    @Test
    public void testGetTermProvider() {
        TermsMap.TermProvider provider = termsMap.getTermProvider("Чистое Качество");

        assertNotNull(provider);
        assertEquals("ии:термин:Чистое Качество", provider.getUri());
    }

    @Test
    public void testGetAll() {
        Set<Map.Entry<String, TermsMap.TermProvider>> set = termsMap.getAll();

        assertTrue(set.size() > 0);
    }

    @Test
    public void testGetTerm() {
        Term term = termsMap.getTerm("Вселенский Межгалактический Диапазон");

        assertNotNull(term);
        assertEquals("ии:термин:Вселенский Межгалактический Диапазон", term.getUri());
    }

    @Test
    public void testGetMainTermProvider() {
        TermsMap.TermProvider provider = aliasesMap.new TermProviderImpl(
                "ии:термин:Тензор", "ии:термин:Тензор напряжённости", false);

        TermsMap.TermProvider mainProvider = provider.getMainTermProvider();
        assertNotNull(mainProvider);
        assertEquals("ии:термин:Тензор напряжённости", mainProvider.getUri());
    }

    @Test
    public void testGetTypeOfTerm() {
        TermsMap.TermProvider provider = aliasesMap.new TermProviderImpl(
                "ии:термин:ТОО-УУ", "ии:термин:Коллективный Космический Разум", false);

        assertEquals(4, provider.getTermType());
    }

    @Test
    public void testGetAliases() {
        TermsMap.TermProvider provider = aliasesMap.new TermProviderImpl(
                "ии:термин:Конфективное ССС-Состояние", null, true);

        List<TermsMap.TermProvider> aliases = provider.getAliases();
        assertEquals("ии:термин:Конфективный", aliases.get(0).getUri());
        assertEquals("ии:термин:Конфективность", aliases.get(1).getUri());
    }

    @Test
    public void testGetAbbreviations() {
        TermsMap.TermProvider provider = aliasesMap.new TermProviderImpl("ии:термин:ФЛУУ-ЛУУ-комплекс", null, true);

        List<TermsMap.TermProvider> abbreviations = provider.getAbbreviations();
        assertEquals("ии:термин:ФЛК", abbreviations.get(0).getUri());
        assertEquals("ии:термин:ФЛ-комплекс", abbreviations.get(1).getUri());
    }

    @Test
    public void testGetCodes() {
        TermsMap.TermProvider provider = aliasesMap.new TermProviderImpl(
                "ии:термин:Мобиллюрасцитный Дубликатор Сектора", null, true);

        TermsMap.TermProvider code = provider.getCode();
        assertEquals("ии:термин:ЮЮ-ИИЙ-ССС-ЮЮ", code.getUri());
    }
}
