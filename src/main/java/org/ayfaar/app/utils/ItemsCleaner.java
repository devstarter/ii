package org.ayfaar.app.utils;

import org.apache.commons.lang.NotImplementedException;

public class ItemsCleaner {
    public static String cleanAll(String value) {
        value = cleanChapters(value);
        // todo: more cleaners
        return value;
    }

    public static String cleanChapters(String value) {
        // fixme: issue#2
        throw new NotImplementedException("issue#2");
    }
}
