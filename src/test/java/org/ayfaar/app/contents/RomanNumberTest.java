package org.ayfaar.app.contents;

import org.junit.Test;

import javax.validation.constraints.Null;

import static org.junit.Assert.*;

/**
 * @author Ruav
 */
public class RomanNumberTest {

    @Test
    public void testParse() throws Exception {

        assertEquals(12, RomanNumber.parse("xIi"));
        assertEquals(10,RomanNumber.parse("X "));
        assertEquals(22,RomanNumber.parse("X xiI"));
        assertEquals(10,RomanNumber.parse("X"));
        assertEquals(1,RomanNumber.parse("I"));
        assertEquals(4, RomanNumber.parse("IV"));
        assertEquals(19, RomanNumber.parse("X I X"));
        assertEquals(9, RomanNumber.parse("IX"));
        assertEquals(14, RomanNumber.parse(" X     I V "));
        assertEquals(49, RomanNumber.parse("X x     x Xi X"));
        assertNotEquals(22, RomanNumber.parse("XXIII"));
        assertNotEquals(23, RomanNumber.parse("XIII"));
    }

    @Test(expected = NullPointerException.class)
    public void testParseNullPointerException() {
        assertEquals(12, RomanNumber.parse(null));
    }

    @Test(expected = NullPointerException.class)
    public void testParseEmptyException() {
        assertEquals(12, RomanNumber.parse(null));
    }

    @Test(expected = NumberFormatException.class)
    public void testParseException() {
        assertNull(RomanNumber.parse("dsf234"));
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

    @Test(expected = NumberFormatException.class)
    public void testConvertToRomanNumberExceptionMin() {
        assertNotEquals("XXXVII", RomanNumber.convertToRomanNumber(Integer.MIN_VALUE));
    }
    @Test(expected = NumberFormatException.class)
    public void testConvertToRomanNumberExceptionMax() {
        assertNotEquals("XXXVII", RomanNumber.convertToRomanNumber(Integer.MAX_VALUE));
    }
    @Test(expected = NullPointerException.class)
    public void testConvertToRomanNullpointerException() {
        assertNull(RomanNumber.convertToRomanNumber(null));
    }
}
