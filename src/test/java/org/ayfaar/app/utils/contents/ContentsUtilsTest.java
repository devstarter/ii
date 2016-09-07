package org.ayfaar.app.utils.contents;


import org.ayfaar.app.IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class ContentsUtilsTest{

    @Autowired
    ContentsUtils contentsUtils;

    @Test   //Test full text
    public void filterLengthWordsAfterTest(){

        String paragraph = "Аспекты Качеств - главная основа Фокусной Динамики.";

        String find = "Аспекты Качеств - главная основа Фокусной Динамики.";
        String s = contentsUtils.filterLengthWordsAfter(paragraph, find, 3);

        assertEquals("Аспекты Качеств - главная основа Фокусной Динамики.", s);
    }

    @Test   //текст из середины, результат должен быть впереди с "..." и сзади плюс три слова и "..."
    public void filterLengthWordsAfterTest1(){


        String paragraph = "Аспекты Качеств - главная основа Фокусной Динамики.";

        String find = "Качеств";
        String s = contentsUtils.filterLengthWordsAfter(paragraph, find, 3);

        assertEquals("...Качеств - главная основа...", s);
    }

    @Test   //текст с конца строки, троеточие подставляется только впереди строки
    public void filterLengthWordsAfterTest2(){


        String paragraph = "Аспекты Качеств - главная основа Фокусной Динамики.";

        String find = "Фокусной Динамики.";
        String s = contentsUtils.filterLengthWordsAfter(paragraph, find, 3);

        assertEquals("...Фокусной Динамики.", s);
    }

    @Test   //текст с начала строки, в результат дописывается еще три слова плюс троеточие подставляется только вконце строки
    public void filterLengthWordsAfterTest3(){

        String paragraph = "Аспекты Качеств - главная основа Фокусной Динамики.";

        String find = "Аспекты";
        String s = contentsUtils.filterLengthWordsAfter(paragraph, find, 3);

        assertEquals("Аспекты Качеств - главная...", s);
    }

    @Test   //поиск не существующего текста, должен выдавать пустую строку
    public void filterLengthWordsAfterTest4(){

        String paragraph = "Аспекты Качеств - главная основа Фокусной Динамики.";

        String find = "sdlkdfsfsdfdsdfjs";
        String s = contentsUtils.filterLengthWordsAfter(paragraph, find, 3);

        assertEquals("", s);
    }
}
