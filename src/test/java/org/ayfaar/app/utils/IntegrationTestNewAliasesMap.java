package org.ayfaar.app.utils;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Term;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@Ignore
public class IntegrationTestNewAliasesMap extends IntegrationTest{
    @Autowired
    NewAliasesMap newAliasesMap;

    /**
     * для запуска этих тестов нужно сделать  метод load в классе NewAliasesMap public
     */
    /*@Before
    public void loadTerms() {
        newAliasesMap.load();
    }*/

    @Test
    public void testGetTermProvider() {
        NewAliasesMap.TermProvider provider = newAliasesMap.getTermProvider("Чистое Качество");

        assertNotNull(provider);
        assertEquals("ии:термин:Чистое Качество", provider.getUri());
    }

    @Test
    public void testGetAllProviders() {
        Set<Map.Entry<String, NewAliasesMap.TermProvider>> set = newAliasesMap.getAllProviders();

        assertTrue(set.size() > 0);
    }

    @Test
    public void testGetTerm() {
        Term term = newAliasesMap.getTerm("Вселенский Межгалактический Диапазон");

        assertNotNull(term);
        assertEquals("ии:термин:Вселенский Межгалактический Диапазон", term.getUri());
    }

    @Test
    public void testGetMainTermProvider() {
        NewAliasesMap.TermProvider provider = newAliasesMap.getMainTermProvider("Тензор напряжённости");

        assertNotNull(provider);
        assertEquals("ии:термин:Тензор", provider.getUri());
        assertNull(provider.getMainTermUri());
    }

    @Test
    public void testGetMainTermProviderWhenMainTermProviderNotExist() {
        NewAliasesMap.TermProvider provider = newAliasesMap.getMainTermProvider("Свилгсон");

        assertNull(provider);
    }

    @Test
    public void testGetTypeOfTerm() {
        assertEquals(4, newAliasesMap.getTermType("ТОО-УУ"));
    }

    @Test
    public void testGetAliases() {
        List<NewAliasesMap.TermProvider> aliases = newAliasesMap.getAliases("ии:термин:Конфективное ССС-Состояние");
        assertEquals("ии:термин:Конфективный", aliases.get(0).getUri());
        assertEquals("ии:термин:Конфективность", aliases.get(1).getUri());
    }

    @Test
    public void testGetAbbreviations() {
        List<NewAliasesMap.TermProvider> aliases = newAliasesMap.getAbbreviations("ии:термин:ФЛУУ-ЛУУ-комплекс");
        assertEquals("ии:термин:ФЛК", aliases.get(0).getUri());
        assertEquals("ии:термин:ФЛ-комплекс", aliases.get(1).getUri());
    }

    @Test
    public void testGetCodes() {
        List<NewAliasesMap.TermProvider> aliases = newAliasesMap.getCodes("ии:термин:Мобиллюрасцитный Дубликатор Сектора");
        assertEquals("ии:термин:ЮЮ-ИИЙ-ССС-ЮЮ", aliases.get(0).getUri());
    }
}
