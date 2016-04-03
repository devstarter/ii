package org.ayfaar.app.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdvanceComparatorTest {

    @Test
    public void compare() {
        final List<String> list = Arrays.asList(
                "Как всё появилось. Часть 14. Пространственно-Временные Континуумы. Планетарный ПВК",
                "Как всё появилось. Часть 6. Образование Фокусов Самосознания и Резомиралов",
                "Как всё появилось. Часть 1. Текст для примера",
                "Как всё появилось. Часть 10. Пространственно Временные Континуумы. Образование Форм Самосознаний");

        Collections.sort(list, new AdvanceComparator());

        System.out.println(list.get(0));
        System.out.println(list.get(1));
        System.out.println(list.get(2));
        System.out.println(list.get(3));

        Assert.assertTrue(list.get(0).contains("Часть 1."));
        Assert.assertTrue(list.get(1).contains("Часть 6."));
        Assert.assertTrue(list.get(2).contains("Часть 10."));
        Assert.assertTrue(list.get(3).contains("Часть 14."));

        final List<String> list1 = Arrays.asList(
                "тект 1. текст 10. текст",
                "тект 1. текст 14. текст",
                "тект 1. текст 1. текст");

        Collections.sort(list1, new AdvanceComparator());

        System.out.println(list1.get(0));
        System.out.println(list1.get(1));
        System.out.println(list1.get(2));

        Assert.assertTrue(list1.get(0).contains(" 1."));
        Assert.assertTrue(list1.get(1).contains(" 10."));
        Assert.assertTrue(list1.get(2).contains(" 14."));
    }
}