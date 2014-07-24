package org.ayfaar.ii.importing;

import org.ayfaar.ii.utils.TermUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TermUtilsTest {
    @Test
    public void testIsCosmicCode() throws Exception {
        assertTrue(TermUtils.isCosmicCode("АЙФААР"));
        assertTrue(TermUtils.isCosmicCode("АА-ЛЛ-ГМ"));
        assertFalse(TermUtils.isCosmicCode("ВСЕ-Воля-ВСЕ-Разума"));
        assertFalse(TermUtils.isCosmicCode("ВВУ-Информация"));
        assertFalse(TermUtils.isCosmicCode("Буддхический"));
    }

    @Test
    public void testIsCosmicCodeAndNonCosmic() throws Exception {
        assertFalse(TermUtils.isComposite("АЙФААР"));
        assertFalse(TermUtils.isComposite("АА-ЛЛ-ГМ"));
        assertFalse(TermUtils.isComposite("Аналогентные Вселенные"));
        assertFalse(TermUtils.isComposite("Аналогентные-Вселенные"));
        assertTrue(TermUtils.isComposite("ААРРГ-показатель"));
        assertTrue(TermUtils.isComposite("ААИИ-СС-М-период"));
        assertTrue(TermUtils.isComposite("СФУУРММ-Форма"));
        assertFalse(TermUtils.isComposite("12 Эволюционных и Инволюционных Ветвей"));
        assertFalse(TermUtils.isComposite("ААГГДА-А-АГДАА"));
    }

    @Test
    public void testGetNonCosmicCodePart() {
        assertEquals("период", TermUtils.getNonCosmicCodePart("ААИИ-СС-М-период"));
        assertEquals("Форма", TermUtils.getNonCosmicCodePart("СФУУРММ-Форма"));
        assertEquals(null, TermUtils.getNonCosmicCodePart("СФУУРММ"));
        assertEquals(null, TermUtils.getNonCosmicCodePart("Форма"));
    }
}
