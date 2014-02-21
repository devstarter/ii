package org.ayfaar.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TermUtils {
    private static final String cosmicCodeRegExp = "[А-ЯЁ-]+";
    private static final String nonCosmicCodeRegExp = ".[а-яё]+";

    public static boolean isCosmicCode(String term) {
        return term != null && !term.isEmpty() && term.matches("^"+cosmicCodeRegExp+"$");
    }

    /**
     * Has Cosmic code and after non cosmic
     * @param term
     * @return
     */
    public static boolean isComposite(String term) {
        return term != null && !term.isEmpty()
                && term.matches("^"+cosmicCodeRegExp+"\\-"+nonCosmicCodeRegExp+"$");
    }

    public static String getNonCosmicCodePart(String term) {
        Matcher matcher = Pattern.compile("^" + cosmicCodeRegExp + "\\-("+nonCosmicCodeRegExp+")$").matcher(term);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
