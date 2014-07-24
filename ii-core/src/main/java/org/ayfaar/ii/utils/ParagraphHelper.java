package org.ayfaar.ii.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParagraphHelper {

    public String getShortName(String name) {
        Matcher matcher = Pattern.compile("^(Параграф\\s\\d+\\.\\d+\\.\\d+)\\.\\s.+").matcher(name);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
