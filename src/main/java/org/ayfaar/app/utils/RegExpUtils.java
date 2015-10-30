package org.ayfaar.app.utils;

public class RegExpUtils {
    public static final String w = "[A-Za-zА-Яа-я0-9Ёё]";
    public static final String W = "[^A-Za-zА-Яа-я0-9Ёё]";

	public static String buildWordContainsRegExp(String q) {
		return "(^" + q + RegExpUtils.W + "+)|(" + RegExpUtils.W + "+" + q + RegExpUtils.W + "+)|(" + RegExpUtils.W + "+" + q + "$)";
	}
}
