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
                "Как все появилось. Часть 14. Пространственно-Временные Континуумы. Планетарный ПВК",
                "Как все появилось. Часть 6. Образование Фокусов Самосознания и Резомиралов",
                "Как все появилось. Часть 1. Текст для примера",
                "Как всё появилось. Часть 10. Пространственно Временные Континуумы. Образование Форм Самосознаний");

        Collections.sort(list, new AdvanceComparator());

        Assert.assertTrue(list.get(0).contains("Часть 1."));
        Assert.assertTrue(list.get(1).contains("Часть 6."));
        Assert.assertTrue(list.get(2).contains("Часть 10."));
        Assert.assertTrue(list.get(3).contains("Часть 14."));
    }
}