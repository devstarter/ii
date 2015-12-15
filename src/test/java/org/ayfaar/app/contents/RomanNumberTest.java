package org.ayfaar.app.contents;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Ruav
 */
public class RomanNumberTest {

    @Test
    public void testParseRomanNumber() throws Exception {
        assertEquals(14,RomanNumber.parseRomanNumber("XIV"));
        assertEquals(10,RomanNumber.parseRomanNumber("X"));
        assertEquals(1,RomanNumber.parseRomanNumber("I"));
        assertEquals(4, RomanNumber.parseRomanNumber("IV"));
        assertEquals(19, RomanNumber.parseRomanNumber("XIX"));
        assertEquals(9, RomanNumber.parseRomanNumber("IX"));
        assertEquals(23, RomanNumber.parseRomanNumber("XXIII"));
        assertEquals(49, RomanNumber.parseRomanNumber("XXXXIX"));
        assertNotEquals(22, RomanNumber.parseRomanNumber("XXIII"));
        assertNotEquals(23, RomanNumber.parseRomanNumber("XIII"));
    }

    @Test
    public void testConvertToRomanNumber() throws Exception {
        assertEquals("XIV", RomanNumber.convertToRomanNumber(14));
        assertEquals("X", RomanNumber.convertToRomanNumber(10));
        assertEquals("I", RomanNumber.convertToRomanNumber(1));
        assertEquals("IV", RomanNumber.convertToRomanNumber(4));
        assertEquals("XIX", RomanNumber.convertToRomanNumber(19));
        assertEquals("IX", RomanNumber.convertToRomanNumber(9));
        assertEquals("XXXXIX", RomanNumber.convertToRomanNumber(49));
        assertEquals("XXIII", RomanNumber.convertToRomanNumber(23));
        assertNotEquals("XXIX", RomanNumber.convertToRomanNumber(34));
        assertNotEquals("XXXVII", RomanNumber.convertToRomanNumber(38));

    }
}
