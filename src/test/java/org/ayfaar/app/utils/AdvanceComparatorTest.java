package org.ayfaar.app.utils;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdvanceComparatorTest {

    @Test
    public void compare() {
        final List<String> list = asList(
                "Как всё появилось. Часть 14. Пространственно-Временные Континуумы. Планетарный ПВК",
                "Как всё появилось. Часть 6. Образование Фокусов Самосознания и Резомиралов",
                "Как всё появилось. Часть 1. Текст для примера",
                "Как всё появилось. Часть 10. Пространственно Временные Континуумы. Образование Форм Самосознаний");

        Collections.sort(list, new AdvanceComparator());

        System.out.println(list.get(0));
        System.out.println(list.get(1));
        System.out.println(list.get(2));
        System.out.println(list.get(3));

        assertTrue(list.get(0).contains("Часть 1."));
        assertTrue(list.get(1).contains("Часть 6."));
        assertTrue(list.get(2).contains("Часть 10."));
        assertTrue(list.get(3).contains("Часть 14."));

        final List<String> list1 = asList(
                "тект 1. текст 10. текст",
                "тект 1. текст 14. текст",
                "тект 1. текст 1. текст");

        Collections.sort(list1, new AdvanceComparator());

        System.out.println(list1.get(0));
        System.out.println(list1.get(1));
        System.out.println(list1.get(2));

        assertTrue(list1.get(0).contains(" 1."));
        assertTrue(list1.get(1).contains(" 10."));
        assertTrue(list1.get(2).contains(" 14."));
    }

    @Test
    public void testNumberTail() {
        final List<String> list = asList("12.9.1.1", "12.9.1.2", "12.9.1.10");
        list.sort(AdvanceComparator.INSTANCE);
        assertEquals("12.9.1.1", list.get(0));
        assertEquals("12.9.1.2", list.get(1));
        assertEquals("12.9.1.10", list.get(2));
    }
}