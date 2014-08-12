package org.ayfaar.app.format;

import org.ayfaar.app.AbstractTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FormatItemsTest extends AbstractTest {
    private static Document doc;
    private final String item10_10001;
    private final String item10_10042;

    public FormatItemsTest() throws IOException {
        doc = Jsoup.parse(getFile("10tom.html"));
        item10_10001 = getFile("10.10001.html");
        item10_10042 = getFile("10.10042.html");
    }

    @Test
    public void format() throws IOException {
        Elements items = doc.select(".par-numbers.char-style-override-3");
        assertTrue(items.size() > 0);
    }

    @Test
    public void first() throws IOException {
        Element item = doc.select(".par-numbers.char-style-override-3").first();
        String number = item.text();
        number = number.substring(0, number.length()-1);
        assertEquals("10.10001", number);
        String formatted = FormatItems.format(item.nextSibling());
        assertEquals(item10_10001, formatted);
    }

    @Test
    public void test10_11806() throws IOException {
        Elements items = doc.select(".par-numbers.char-style-override-3");
        for (Element item : items) {
            if (item.text().equals("10.11806.")) {
                String formatted = FormatItems.format(item.nextSibling());
                System.out.println(formatted);
            }
        }

    }

    // убрать ссылки на http://www.ayfaar.org/wiki/... например в пункте 10.10031.
    // оставить италик напрмер <span class="italic">informare»</span> в 10.10042.
    // оставить италик и подчёркнутый напрмер <span class="italic-und">недискретного</span> в 10.10083.
}
