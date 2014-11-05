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
import static org.junit.Assert.assertEquals;

@Ignore
public class TestNewAliasesMap extends IntegrationTest{
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
        NewTermsMap.TermProvider provider = newAliasesMap.getTermProvider("Чистое Качество");

        assertNotNull(provider);
        assertEquals("ии:термин:Чистое Качество", provider.getUri());
        assertNotNull(provider.getTerm());
    }

    @Test
    public void testGetAll() {
        Set<Map.Entry<String, NewTermsMap.TermProvider>> set = newAliasesMap.getAll();

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
    public void testGetAliasesTermProviders() {
        List<NewAliasesMap.TermProvider> aliases = newAliasesMap.new TermProviderImpl(
                "ии:термин:Конфективное ССС-Состояние", null, true).getAliasTermProviders();

        assertEquals("ии:термин:Конфективный", aliases.get(0).getUri());
        assertEquals("ии:термин:Конфективность", aliases.get(1).getUri());
    }

    @Test
    public void testGetAbbreviationTermProviders() {
        List<NewAliasesMap.TermProvider> abbreviations = newAliasesMap.new TermProviderImpl(
                "ии:термин:ФЛУУ-ЛУУ-комплекс", null, true).getAbbreviationTermProviders();

        assertEquals("ии:термин:ФЛК", abbreviations.get(0).getUri());
        assertEquals("ии:термин:ФЛ-комплекс", abbreviations.get(1).getUri());
    }

    @Test
    public void testGetCodeTermProviders() {
        List<NewAliasesMap.TermProvider> codes = newAliasesMap.new TermProviderImpl(
                "ии:термин:Мобиллюрасцитный Дубликатор Сектора", null, true).getCodeTermProviders();

        assertEquals("ии:термин:ЮЮ-ИИЙ-ССС-ЮЮ", codes.get(0).getUri());
    }
}


