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

    public FormatItemsTest() throws IOException {
        doc = Jsoup.parse(getFile("10tom.html"));
        html10_10001 = getFile("10.10001.html");
        text10_10001 = getFile("10.10001.txt");
        item10_10042 = getFile("10.10042.html");
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
        assertEquals(text10_10001, unformatted);
    }

    private Element getItemHtmlElement(String itemNumber) {
        return doc.select(".par-numbers.char-style-override-3:contains("+itemNumber+")").get(0);
    }

    @Test
    public void test10_11806() throws IOException {
        String formatted = FormatItems.format(getItemHtmlElement("10.11806"));
        System.out.println(formatted);

    }

    // todo написать тесты для этих случаев
    // убрать ссылки на http://www.ayfaar.org/wiki/... например в пункте 10.10031.
    // оставить италик напрмер <span class="italic">informare»</span> в 10.10042.
    // оставить италик и подчёркнутый например <span class="italic-und">недискретного</span> в 10.10083.
}
