package org.ayfaar.app.utils;

import org.apache.commons.lang.NotImplementedException;

public class ItemsCleaner {

    public static String clean(String value) {
        // fixme: issue#2
        //throw new NotImplementedException("issue#2");

        String[] str = value.split("\n");
        return str[0];
    }
}
