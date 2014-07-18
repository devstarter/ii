package org.ayfaar.app.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpTest {

    @Test
    public void test() {
        String query = "НААСМ";
        String regexp = "";
        for (int i=0; i< query.length(); i++) {
            if (i > 0 && query.charAt(i) == query.charAt(i-1)) {
                continue;
            }
            regexp += "("+query.charAt(i)+")*";
        }
        Assert.assertEquals("(Н)*(А)*(С)*(М)*", regexp);

        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }
}
