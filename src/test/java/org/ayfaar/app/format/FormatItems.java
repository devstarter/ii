package org.ayfaar.app.format;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * Задача класа вернуть форматированный текст (италик и подчёркивание) для все пунктов. Сейчас в Item
 */
public class FormatItems {
    private static final String END = "^END^";

    public static String format(Node item) {
        String formatted = _format(item);
        formatted = formatted.replace(END, "");
        formatted = formatted.replaceAll("<ol><li>", "");
        formatted = formatted.replaceAll("</li>\\s*</ol>", "");
        formatted = formatted.replaceAll("^<li>", "");
        formatted = formatted.replaceAll("</li>\\s*$", "");
        return formatted;
    }
    private static String _format(Node item) {
        StringBuilder sb = new StringBuilder();
        while (item != null) {
            Element el = item instanceof Element ? (Element) item : null;
            if (el != null) {
                if ("par-numbers char-style-override-3".equals(((Element) item).className())) {
                    sb.append(END);
                    return sb.toString();
                }
                else if (el.children().size() > 0) {
                    String formattedChild = _format(el.childNode(0));
                    if (!formattedChild.isEmpty()) {
//                        sb.append(String.format("<%s>%s</%s>", el.nodeName(), formattedChild, el.nodeName()));
                        sb.append(formattedChild);
                        if (formattedChild.contains(END)) {
                            return sb.toString();
                        }
                    }
                } else if (el.text().isEmpty()) {
                    sb.append(" ");
                } else {
                    sb.append(el.outerHtml());
                }
            } else {
                sb.append(item);
            }
            if (item.nextSibling() == null) {
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
