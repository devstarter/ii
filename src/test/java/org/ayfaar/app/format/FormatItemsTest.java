package org.ayfaar.app.format;

import org.ayfaar.app.AbstractTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 *  Для тестирования возьмём 10 том в формате html, который лежит в ресурсах к тесту
 * (src/test/resources/org/ayfaar/app/format/10tom.html).
 */
public class FormatItemsTest extends AbstractTest {
    private static Document doc;
    private final String html10_10001;
    private final String item10_10042;
    private final String text10_10001;

    private final String text10_10001_2;
    private final String text10_10031;
    private final String text10_10183;

    public FormatItemsTest() throws IOException {
        doc = Jsoup.parse(getFile("10tom.html"));
        html10_10001 = getFile("10.10001.html");
        text10_10001 = getFile("10.10001.txt");
        text10_10001_2 = getFile("10.10001_2.txt");
        item10_10042 = getFile("10.10042.html");
        text10_10031 = getFile("10.10031.txt");
        text10_10183 = getFile("10.10183.txt");

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
        String unformatted = Jsoup.clean(formatted, Whitelist.none());
        // нужно помимо очистки от тегов приобразовать коды типа &laquo; в текстовое представление типа "
        // возможно поможет клас HtmlCharacterEntityDecoder
        unformatted = org.springframework.web.util.HtmlUtils.htmlUnescape(unformatted);
        //assertEquals(text10_10001, unformatted);
        //10.10001.txt - не соответс тексту из 10tom.html, 10.10001_2.txt - соответс, отличие: ... и символ многоточие
        assertEquals(text10_10001_2, unformatted);
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
        assertEquals(text10_10031, formatted);
    }

    @Test
    /*
    оставить италик напрмер <span class="italic">informare»</span> в 10.10042.
     */
    public void test10_10_10042() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.10042"));

        formatted = org.springframework.web.util.HtmlUtils.htmlUnescape(formatted);
        assertEquals(item10_10042, formatted);
    }

    @Test
    /*
    оставить италик и подчёркнутый например <span class="italic-und">недискретного</span> в 10.10083.
     */
    public void test10_10_10183() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.10183"));

        formatted = org.springframework.web.util.HtmlUtils.htmlUnescape(formatted);
        assertEquals(text10_10183, formatted);
    }

    // todo написать тесты для этих случаев
    // убрать ссылки на http://www.ayfaar.org/wiki/... например в пункте 10.10031.
    // оставить италик напрмер <span class="italic">informare»</span> в 10.10042.
    // оставить италик и подчёркнутый например <span class="italic-und">недискретного</span> в 10.10083.
}
