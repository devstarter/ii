package org.ayfaar.app.utils;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.ayfaar.app.utils.TermsMap.TermProvider;
import static org.junit.Assert.*;

public class NewAliasesMapIntegrationTest extends IntegrationTest{
    @Autowired
    private TermsMapImpl aliasesMap;
    @Autowired
    private TermsMap termsMap;

    @Test
    public void testGetTermProvider() {
        TermProvider provider = termsMap.getTermProvider("Чистое Качество");

        assertNotNull(provider);
        assertEquals("ии:термин:Чистое Качество", provider.getUri());
    }

    @Test
    public void testGetAll() {
        List<Map.Entry<String, TermProvider>> set = termsMap.getAll();

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
        TermProvider provider = aliasesMap.new TermProviderImpl(
                "ии:термин:Тензор", "ии:термин:Тензор напряжённости", false);

        TermProvider mainProvider = provider.getMainTermProvider();
        assertNotNull(mainProvider);
        assertEquals("ии:термин:Тензор напряжённости", mainProvider.getUri());
    }

    @Test
    public void testGetTypeOfTerm() {
        final TermProvider provider = termsMap.getTermProvider("ТОО-УУ");

        assertEquals("ТОО-УУ", provider.getName());
        assertEquals(UriGenerator.generate(Term.class, "ТОО-УУ"), provider.getUri());
        assertEquals(Link.CODE, provider.getType());
        assertTrue(provider.isCode());
        assertFalse(provider.isAbbreviation());
        assertFalse(provider.isAlias());
        assertTrue(provider.hasMainTerm());
        assertNotNull(provider.getMainTermProvider());
        assertEquals(UriGenerator.generate(Term.class, "Коллективный Космический Разум"), provider.getMainTermProvider().getUri());
        assertEquals("ТОО-УУ", provider.getTerm().getName());
//        assertEquals(0, provider.getAliases().size());
        assertEquals(0, provider.getAbbreviations().size());
        assertNull(provider.getCode());
        assertFalse(provider.hasShortDescription());
    }

    @Test
    public void testGetAliases() {
        TermProvider provider = termsMap.getTermProvider("Конфективное ССС-Состояние");

        List<TermProvider> aliases = provider.getAliases();
        assertEquals("ии:термин:Конфективный", aliases.get(0).getUri());
        assertEquals("ии:термин:Конфективность", aliases.get(1).getUri());
    }

    @Test
    public void testGetAbbreviations() {
        TermProvider provider = termsMap.getTermProvider("ФЛУУ-ЛУУ-комплекс");

        List<TermProvider> abbreviations = provider.getAbbreviations();
        assertEquals("ии:термин:ФЛК", abbreviations.get(0).getUri());
        assertEquals("ии:термин:ФЛ-комплекс", abbreviations.get(1).getUri());
    }

    @Test
    public void testGetCodes() {
        TermProvider provider = termsMap.getTermProvider("Мобиллюрасцитный Дубликатор Сектора");

        TermProvider code = provider.getCode();
        assertEquals("ии:термин:ЮЮ-ИИЙ-ССС-ЮЮ", code.getUri());
    }

    @Test
    public void testRA() {
        final TermProvider ra = termsMap.getTermProvider("РА");
        assertTrue(ra.getMainTermProvider().hasShortDescription());
    }

    @Test
    public void sameTermProviders() {
        final TermProvider morph = termsMap.getTermProvider("резонационной Активности");
        final TermProvider origin = termsMap.getTermProvider("резонационная Активность");
        final TermProvider abbr = termsMap.getTermProvider("РА");

        assertEquals(morph, origin);
        assertEquals(abbr.getMainTermProvider(), origin);
    }

    @Test
    public void testGetMorphs() {
        TermProvider provider = termsMap.getTermProvider("Время");
        List<String> morphs = provider.getMorphs();

        assertEquals(8, morphs.size());
    }
}
