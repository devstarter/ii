package org.ayfaar.app.format;

import org.ayfaar.app.AbstractTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.IOException;

import static org.ayfaar.app.format.FormatItems.unformat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *  Для тестирования возьмём 10 том в формате html, который лежит в ресурсах к тесту
 * (src/test/resources/org/ayfaar/app/format/10tom.html).
 */
public class FormatItemsTest extends AbstractTest {
    private static Document doc;
    private final String html10_10001;
    private final String html10_10042;
    private final String text10_10001;

    private final String text10_10031;
    private final String text10_10183;
    private final String item10_10183;
    private final String text10_10042;
    private final String html10_10031;
    private final String html10_11255;

    public FormatItemsTest() throws IOException {
        doc = Jsoup.parse(getFile("10tom.html"));
        html10_10001 = getFile("10.10001.html");
        text10_10001 = getFile("10.10001.txt");
        html10_10042 = getFile("10.10042.html");
        text10_10031 = getFile("10.10031.txt");
        text10_10183 = getFile("10.10183.txt");
        item10_10183 = getFile("10.10183.html");
        text10_10042 = getFile("10.10042.txt");
        //10.10183.html и 10.10042_2.html по примеру 10.10001.html с &laquo; вместо "

        html10_10031 = getFile("10.10031.html");
        html10_11255 = getFile("10.11255.html");

    }

    @Test
    /*
    тест не проходит так как нужно убрать обрамления пробелов (<span class="char-style-override-4"> </span>)
     */
    public void first() throws IOException {
        Element item = getItemHtmlElement("10.10001");
        String number = item.text();
        number = number.substring(0, number.length()-1);
        assertEquals("10.10001", number);
        String formatted = FormatItems.format(item);
        assertEquals(html10_10001, formatted);
        assertEquals(text10_10001, unformat(formatted));
    }

    private Element getItemHtmlElement(String itemNumber) {
        return doc.select(".par-numbers.char-style-override-3:contains("+itemNumber+")").get(0);
    }

    @Test
    public void test10_11806() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.11806"));
        System.out.println(formatted);
    }

    @Test
    /*
     // убрать ссылки на http://www.ayfaar.org/wiki/... например в пункте 10.10031.
     */
    public void test10_10031() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.10031"));
        assertFalse(formatted.contains("http://www.ayfaar.org/wiki"));
        assertEquals(html10_10031, formatted);
        assertEquals(text10_10031, unformat(formatted));
    }

    @Test
    /*
     убрать вовсе такого рода ссылки
    <span class="footnote-number"><a id="footnote-236458-2-backlink" class="footnote-link" href="#footnote-236458-2">2</a></span>
     */
    public void test10_11255() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.11255"));
        //String formatted = FormatItems.format(getItemHtmlElement("10.11805"));
        assertEquals(html10_11255, formatted);
    }

    @Test
    /*
    оставить италик напрмер <span class="italic">informare»</span> в 10.10042.
     */
    public void test10_10_10042() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.10042"));
        assertEquals(html10_10042, formatted);
        assertEquals(text10_10042, unformat(formatted));
    }

    @Test
    /*
    оставить италик и подчёркнутый например <span class="italic-und">недискретного</span> в 10.10083.
     */
    public void test10_10_10183() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.10183"));
        assertEquals(item10_10183, formatted);
        assertEquals(text10_10183, unformat(formatted));
    }
}
