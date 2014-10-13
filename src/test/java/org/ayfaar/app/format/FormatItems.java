package org.ayfaar.app.format;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.web.util.HtmlUtils.htmlUnescape;

/**
 * Задача класа вернуть форматированный (html) текст для всеx пунктов. Сейчас в Item.content храниться текст без
 * форматирования, в Item.html будет хранится форматированный текст.
 *
 * Логика состоит в том что-бы разделить весь html тест на пункты. И вернуть только то что относится к данному пункту.
 */
public class FormatItems {
    // знак окончания пункта
    private static final String END = "^END^";
    private static Document doc;

    public static String format(Node item) {
        item = item.nextSibling();
        String formatted = formatInternal(item);
        formatted = formatted.replace(END, "");
        formatted = formatted.replaceAll("<ol><li>", "");
        formatted = formatted.replaceAll("</li>\\s*</ol>", "");
        formatted = formatted.replaceAll("^<li>", "");
        formatted = formatted.replaceAll("</li>\\s*$", "");

        formatted = formatted.replaceAll("<span class=\"char-style-override-4\">\\s*</span>", " ");
        formatted = formatted.replaceAll("(<a.*href=\"http://www.ayfaar.org/wiki.*\">)(.*)(</a>)", "$2");
        formatted = formatted.replaceAll("<li><span><a id=\"footnote-236458.+</a></span>", "");

        return formatted;
    }

    /**
     * @param item ветвь документа html, с которой нужно начать полученик форматированного текста
     * @return форматированный текст
     */
    private static String formatInternal(Node item) {
        StringBuilder sb = new StringBuilder();
        while (item != null) {
            Element el = item instanceof Element ? (Element) item : null;
            if (el != null) {
                // par-numbers char-style-override-3 это классы, которыми помечен span номера абраза.
                final String className = ((Element) item).className();
                if (className != null && className.matches("(^par-numbers\\schar-style-override-[38]$)|(H1)")) {
                    // значит это уже следующий пункт, по этому ставим метку окончания.
                    sb.append(END);
                    return sb.toString();
                }
                else if (el.children().size() > 0) {
                    String formattedChild = formatInternal(el.childNode(0));
                    if (!formattedChild.isEmpty()) {
//                        sb.append(String.format("<%s>%s</%s>", el.nodeName(), formattedChild, el.nodeName()));
                        sb.append(formattedChild);
                        // если в дочернем елементе найдено начало следующего пункта, то заканчиваем поиск
                        if (formattedChild.contains(END)) {
                            return sb.toString();
                        }
                    }
                } else {
                    sb.append(el.outerHtml());
                }
            } else {
                sb.append(item);
            }
            // Переключаемся на следующий ближайший елемент в html
            if (item.nextSibling() == null) {
                // если в этом родительском теге уже нет следующих, то переключаемся на следующий после родительского тег
                sb = new StringBuilder(String.format(
                        "<%s>%s</%s>", item.parent().nodeName(), sb.toString(), item.parent().nodeName()));
                item = item.parent().nextSibling();
            } else {
                item = item.nextSibling();
            }
        }
        return sb.toString();
    }

    static String unformat(String formatted){
        String unformatted = Jsoup.clean(formatted, Whitelist.none());
        // нужно помимо очистки от тегов приобразовать коды типа &laquo; в текстовое представление типа "
        // возможно поможет клас HtmlCharacterEntityDecoder
        unformatted = htmlUnescape(unformatted);
        unformatted = unformatted.replaceAll("…", "...");
        return unformatted;
    }

    public static void open(String html) {
        doc = Jsoup.parse(html);
    }

    public static Element getItemHtmlElement(String itemNumber) {
        Elements select = doc.select(".par-numbers.char-style-override-3:contains(" + itemNumber + ")");
        if (select.size() == 0) {
            select = doc.select(".par-numbers.char-style-override-8:contains(" + itemNumber + ")");

        }
        return select.get(0);
    }

    public static void open(InputStream inputStream) throws IOException {
        doc = Jsoup.parse(inputStream, "UTF-8", "");
    }

    public static String format(String number) {
        return format(getItemHtmlElement(number));
    }
}
