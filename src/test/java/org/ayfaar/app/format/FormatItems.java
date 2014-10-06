package org.ayfaar.app.format;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * Задача класа вернуть форматированный (html) текст для всеx пунктов. Сейчас в Item.content храниться текст без
 * форматирования, в Item.html будет хранится форматированный текст.
 *
 * Логика состоит в том что-бы разделить весь html тест на пункты. И вернуть только то что относится к данному пункту.
 */
public class FormatItems {
    // знак окончания пункта
    private static final String END = "^END^";

    public static String format(Node item) {
        item = item.nextSibling();
        String formatted = _format(item);
        formatted = formatted.replace(END, "");
        formatted = formatted.replaceAll("<ol><li>", "");
        formatted = formatted.replaceAll("</li>\\s*</ol>", "");
        formatted = formatted.replaceAll("^<li>", "");
        formatted = formatted.replaceAll("</li>\\s*$", "");

        formatted = formatted.replaceAll("<span class=\"char-style-override-4\">\\s*</span>", " ");
        formatted = formatted.replaceAll("(<a.*href=\"http://www.ayfaar.org/wiki.*\">)(.*)(</a>)", "$2");

        return formatted;
    }

    /**
     * @param item ветвь документа html, с которой нужно начать полученик форматированного текста
     * @return форматированный текст
     */
    private static String _format(Node item) {
        StringBuilder sb = new StringBuilder();
        while (item != null) {
            Element el = item instanceof Element ? (Element) item : null;
            if (el != null) {
                // par-numbers char-style-override-3 это классы, которыми помечен span номера абраза.
                if ("par-numbers char-style-override-3".equals(((Element) item).className())) {
                    // значит это уже следующий пункт, по этому ставим метку окончания.
                    sb.append(END);
                    return sb.toString();
                }
                else if (el.children().size() > 0) {
                    String formattedChild = _format(el.childNode(0));
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
}
