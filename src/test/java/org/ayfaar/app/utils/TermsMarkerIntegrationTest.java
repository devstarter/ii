package org.ayfaar.app.utils;

import org.ayfaar.app.IntegrationTest;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

public class TermsMarkerIntegrationTest extends IntegrationTest {
    @Inject
    TermsMarker termsMarker;

    @Test
    public void test() {
        final String text = "Например, Формо-Творцы АДД-МАДД-ФЛУЙФ-Уровня, организующие высшие ГЛЭИИЙО-реальности";
        final String expected = "Например, <term id=\"Формо-Творец\">Формо-Творцы</term> " +
                "АДД-МАДД-ФЛУЙФ-Уровня, организующие высшие <term id=\"ГЛЭИИЙО\">ГЛЭИИЙО-реальности</term>";

        final String actual = termsMarker.mark(text);

        assertEquals(expected, actual);
    }
}
