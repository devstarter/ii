package org.ayfaar.app.format;

import org.jsoup.nodes.Element;

public class FormatItems {
    public static String format(Element item) {
        StringBuilder sb = new StringBuilder();
        while (item != null) {
            if (item.text().equals(" ")) {
                sb.append(" ");
            } else {
                sb.append(item.html());
            }
            item = item.nextElementSibling();
        }
        return sb.toString();
    }
}
