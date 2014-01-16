package org.ayfaar.app.utils;

public class ProbabilityUtils {

    public static Boolean will(Double value) {
        return Math.random() <= value;
    }
}
