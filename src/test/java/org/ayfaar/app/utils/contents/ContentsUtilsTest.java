package org.ayfaar.app.utils.contents;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;

public class ContentsUtilsTest{

    @Autowired
    ContentsUtils contentsUtils;

    @Test
    public void splitToSentenceTest(){
        //check in first sentence
        String paragraph = "Ваше Самосознание - Дорога к Самим Себе. Право абсолютной Свободы Воли. " +
                "Только вы сами активизируете все сценарии своей Жизни. Главная Суть всех Форм Существования. " +
                "Божий Промысел в ииссиидиологическом понимании - это Принцип аттракторности. " +
                "Позитивные мотивации - это эгллеролифтивный Импульс.";
        String find = "Самосознание";
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        String s = contentsUtils.splitToSentence(paragraph, find);
        long end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

        assertEquals("Ваше Самосознание - Дорога к Самим Себе.", s);
        //System.out.println("Test0: " + String.valueOf(end - start));
    }

    @Test
    public void splitToSentenceTest1(){
        //check inner sentence
        String paragraph = "Ваше Самосознание - Дорога к Самим Себе. Право абсолютной Свободы Воли. " +
                "Только вы сами активизируете все сценарии своей Жизни. Главная Суть всех Форм Существования. " +
                "Божий Промысел в ииссиидиологическом понимании - это Принцип аттракторности. " +
                "Позитивные мотивации - это эгллеролифтивный Импульс.";
        String find = "аттракт";
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        String s = contentsUtils.splitToSentence(paragraph, find);
        long end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

        assertEquals("Божий Промысел в ииссиидиологическом понимании - это Принцип аттракторности.", s);
        //System.out.println("Test1: " + String.valueOf(end - start));
    }

    @Test
    public void splitToSentenceTest2(){
        //check last sentence
        String paragraph = "Ваше Самосознание - Дорога к Самим Себе. Право абсолютной Свободы Воли. " +
                "Только вы сами активизируете все сценарии своей Жизни. Главная Суть всех Форм Существования. " +
                "Божий Промысел в ииссиидиологическом понимании - это Принцип аттракторности. " +
                "Позитивные мотивации - это эгллеролифтивный Импульс.";
        String find = "эгллеролиф";
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        String s = contentsUtils.splitToSentence(paragraph, find);
        long end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

        assertEquals("Позитивные мотивации - это эгллеролифтивный Импульс.", s);
        //System.out.println("Test2: " + String.valueOf(end - start));
    }

    @Test
    public void splitToSentenceTest3(){
        //check not found
        String paragraph = "Ваше Самосознание - Дорога к Самим Себе. Право абсолютной Свободы Воли. " +
                "Только вы сами активизируете все сценарии своей Жизни. Главная Суть всех Форм Существования. " +
                "Божий Промысел в ииссиидиологическом понимании - это Принцип аттракторности. " +
                "Позитивные мотивации - это эгллеролифтивный Импульс.";
        String find = "dfghfgdfg";
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        String s = contentsUtils.splitToSentence(paragraph, find);
        long end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

        assertEquals("", s);
        //System.out.println("Test3: " + String.valueOf(end - start));
    }
}
