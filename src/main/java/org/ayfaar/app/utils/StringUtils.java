package org.ayfaar.app.utils;

public class StringUtils {
    public static String removeAllNewLines(String str) {
        return str != null ? str.replaceAll("\n|\r|\r\n", "") : null;
    }
}
