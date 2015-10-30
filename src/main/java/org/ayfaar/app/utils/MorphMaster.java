package org.ayfaar.app.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MorphMaster {
    private final static String[] endings = new String[]{"ый","ость","остью","ости","о","ы","ые","ых","ым","ыми","ого","ом"};

    public static Set<String> getAllForms(String word) {
        for (String ending : endings) {
            if (word.endsWith(ending)) {
                return buildForRoot(word.substring(0, word.lastIndexOf(ending)));
            }
        }
        return new HashSet<String>(Arrays.asList(word));
    }

    private static Set<String> buildForRoot(String root) {
        Set<String> forms = new HashSet<String>();
        for (String ending : endings) {
            forms.add(root+ending);
        }
        return forms;
    }
}
