package org.ayfaar.app.utils;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Term;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.ayfaar.app.utils.TermService.TermProvider;
import static org.junit.Assert.*;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "SpringJavaAutowiredMembersInspection"})
public class NewAliasesMapIntegrationTest extends IntegrationTest{
    @Autowired TermServiceImpl aliasesMap;
    @Autowired TermService termService;

    @Test
    public void testGetTermProvider() {
        Optional<TermProvider> provider = termService.get("Чистое Качество");

        assertNotNull(provider);
        assertEquals("ии:термин:Чистое Качество", provider.get().getUri());
    }

    @Test
    public void testGetAll() {
        List<Map.Entry<String, TermProvider>> set = termService.getAll();

        assertTrue(set.size() > 0);
    }

    @Test
    public void testGetTerm() {
        Term term = termService.getTerm("Вселенский Межгалактический Диапазон");

        assertNotNull(term);
        assertEquals("ии:термин:Вселенский Межгалактический Диапазон", term.getUri());
    }

    @Test
    public void testGetMainTermProvider() {
        TermProvider provider = aliasesMap.new TermProviderImpl(
                "ии:термин:Тензор", "ии:термин:Тензор напряжённости", false);

        TermProvider mainProvider = provider.getMainTerm().get();
        assertNotNull(mainProvider);
        assertEquals("ии:термин:Тензор напряжённости", mainProvider.getUri());
    }

    @Test
    public void testGetTypeOfTerm() {
        final TermProvider provider = termService.get("ТОО-УУ").get();

        assertEquals("ТОО-УУ", provider.getName());
        assertEquals(UriGenerator.generate(Term.class, "ТОО-УУ"), provider.getUri());
        assertEquals(LinkType.CODE, provider.getType());
        assertTrue(provider.isCode());
        assertFalse(provider.isAbbreviation());
        assertFalse(provider.isAlias());
        assertTrue(provider.hasMainTerm());
        assertNotNull(provider.getMainTerm());
        assertEquals(UriGenerator.generate(Term.class, "Коллективный Космический Разум"), provider.getMainTerm().get().getUri());
        assertEquals("ТОО-УУ", provider.getTerm().getName());
//        assertEquals(0, provider.getAliases().size());
        assertEquals(0, provider.getAbbreviations().size());
        assertNull(provider.getCode());
        assertFalse(provider.hasShortDescription());
    }

    @Test
    public void testGetAliases() {
        TermProvider provider = termService.get("Конфективное ССС-Состояние").get();

        List<TermProvider> aliases = provider.getAliases();
        assertEquals("ии:термин:Конфективный", aliases.get(0).getUri());
        assertEquals("ии:термин:Конфективность", aliases.get(1).getUri());
    }

    @Test
    public void testGetAbbreviations() {
        TermProvider provider = termService.get("ФЛУУ-ЛУУ-комплекс").get();

        List<TermProvider> abbreviations = provider.getAbbreviations();
        assertEquals("ии:термин:ФЛК", abbreviations.get(0).getUri());
        assertEquals("ии:термин:ФЛ-комплекс", abbreviations.get(1).getUri());
    }

    @Test
    public void testGetCodes() {
        TermProvider provider = termService.get("Мобиллюрасцитный Дубликатор Сектора").get();

        TermProvider code = provider.getCode();
        assertEquals("ии:термин:ЮЮ-ИИЙ-ССС-ЮЮ", code.getUri());
    }

    @Test
    public void testRA() {
        final TermProvider ra = termService.get("РА").get();
        assertTrue(ra.getMainTerm().get().hasShortDescription());
    }

    @Test
    public void sameTermProviders() {
        final TermProvider morph = termService.get("резонационной Активности").get();
        final TermProvider origin = termService.get("резонационная Активность").get();
        final TermProvider abbr = termService.get("РА").get();

        assertEquals(morph, origin);
        assertEquals(abbr.getMainTerm().get(), origin);
    }

    @Test
    public void testGetMorphs() {
        TermProvider provider = termService.get("Время").get();
        List<String> morphs = provider.getMorphs();

        assertEquals(8, morphs.size());
    }
}
