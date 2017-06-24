package org.ayfaar.app.utils;


import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleServiceRegExpTest{

    //String regexp = "\\d{4}-\\d{2}-\\d{2}(_\\d{1,2})?([-_][km])?";
    String regexp = GoogleService.codeVideoPatternRegExp;

    @Test
    public void RegExpTest() {
        String query = "2016-03-24_06-k";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void RegExpTestSub() {
        String query = "2016-03-24_06a-k";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void RegExpTest1() {
        String query = "2016-03-24_06";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void RegExpTestSub1() {
        String query = "2016-03-24_06a";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void RegExpTestSubRus() {
        String query = "2016-03-24_06а";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void RegExpTest2() {
        String query = "2016-03-24";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void RegExpTest3() {
        String query = "2016-03-24-k";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void RegExpTest4() {
        String query = "2016-03";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void RegExpTest8() {
        String query = "2016-03-01-kk";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void RegExpTest9() {
        String query = "02016-03-01";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void RegExpTest5() {
        String query = "Some text";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void RegExpTest6() {
        String query = " ";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void RegExpTest7() {
        String query = "2014-10-15_1";
        Matcher matcher = Pattern.compile(regexp).matcher(query);
        Assert.assertTrue(matcher.find());
    }

}
