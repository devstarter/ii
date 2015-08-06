package org.ayfaar.app.utils;

public class StringUtils {
    public static String removeAllNewLines(String str) {
        return str != null ? str.replaceAll("\n|\r|\r\n", "") : null;
    }

    public static String trim(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.trim();
    }

    public static String trim(String text, String subjectToTrim) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        if (text.endsWith(subjectToTrim)) {
            final int i = text.lastIndexOf(subjectToTrim);
            return text.substring(0, i);
        }
        return text;
    }
}
