package org.ayfaar.app.utils;

public class ItemsCleaner {
    public static String clean(String value) {
        String[] str = value.split("\n");

        if(value.length() == 0) {
            return null;
        }
        return str[0];
    }
}
