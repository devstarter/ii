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
        NewAliasesMap.TermProvider provider = newAliasesMap.getTermProvider("Чистое Качество");

        assertNotNull(provider);
        assertEquals("ии:термин:Чистое Качество", provider.getUri());
        assertNotNull(provider.getTerm());
    }

    @Test
    public void testGetAll() {
        Set<Map.Entry<String, NewAliasesMap.TermProvider>> set = newAliasesMap.getAll();

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
        assertEquals(99, newAliasesMap.getAliasTermProviders().size());
    }

    @Test
    public void testGetAbbreviationTermProviders() {
        assertEquals(60, newAliasesMap.getAbbreviationTermProviders().size());
    }

    @Test
    public void testGetCodeTermProviders() {
        assertEquals(42, newAliasesMap.getCodeTermProviders().size());
    }
}
