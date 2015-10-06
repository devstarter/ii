package org.ayfaar.app.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

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


    public static String markWithStrong(String text, List<String> queries) {
		List<String> sortedQueries = new ArrayList<String>(queries);
		Collections.sort(sortedQueries, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return Integer.compare(o2.length(), o1.length());
			}
		});
		String regexp = "";
		for (String query : sortedQueries) {
			regexp += "|("+query+")";
		}
		regexp = regexp.replaceFirst("\\|", "");

		text = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
					.matcher(text)
					.replaceAll("<strong>$0</strong>");
		return text;
    }
}
