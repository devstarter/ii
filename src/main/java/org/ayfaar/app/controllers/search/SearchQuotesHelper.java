package org.ayfaar.app.controllers.search;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.RegExpUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SearchQuotesHelper {
    public static final int MAX_WORDS_ON_BOUNDARIES = 30;
    private String forCreateLeftPartQuote = "([^\\.\\?!]*)([\\.\\?!]*)(\\.|\\?|\\!)(\\)|\\»)";
    private String forCreateRightPartQuote = "(\\)|\\»)([^\\.\\?!]*)([\\.\\?!]*)";

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        List<Quote> quotes = new ArrayList<Quote>();
        String uri = "ии:пункт:";
        String forLeftPart = "([\\.\\?!]*)([^\\.\\?!]*)(<strong>)";
        String forRightPart = "(<strong>)([^\\.\\?!]*)([\\.\\?!]*)";
        String regexp = createRegExp(allPossibleSearchQueries);

        for (Item item : foundedItems) {
            String content = "";
            Pattern pattern = Pattern.compile("(^" + regexp + ")|(" + RegExpUtils.W + "+" + regexp + RegExpUtils.W +
                    "+)|(" + regexp + "$)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(item.getContent());

            if (matcher.find()) {
                content = item.getContent().replaceAll("(?iu)\\b(" + regexp + ")\\b", "<strong>$1</strong>");
            }

            String[] phrases = content.split("<strong>");
            String firstPart = createPartQuote(phrases[0] + "<strong>", forLeftPart, "", "left");

            if(firstPart.charAt(0) == '.' || firstPart.charAt(0) == '?' || firstPart.charAt(0) == '!') {
                firstPart = firstPart.substring(1, firstPart.length());
                firstPart = firstPart.trim();
            }

            String[] first = firstPart.split(" ");
            String newFirstPart = "";

            if(first.length > MAX_WORDS_ON_BOUNDARIES + 1) {
                for(int i = first.length - (MAX_WORDS_ON_BOUNDARIES + 1); i < first.length; i++) {
                    newFirstPart += first[i] + " ";
                }
                newFirstPart = newFirstPart.trim();
                firstPart = "..." + newFirstPart.substring(0, newFirstPart.length() - 8).trim();
            }
            else {
                firstPart = firstPart.substring(0, firstPart.length() - 8).trim();
            }

            String lastPart = createPartQuote("<strong>" + phrases[phrases.length-1], forRightPart, "", "right");
            String[] last = lastPart.split(" ");
            String newLastPart = "";

            if(last.length > MAX_WORDS_ON_BOUNDARIES + 1) {
                for(int i = 0; i < MAX_WORDS_ON_BOUNDARIES + 1; i++) {
                    newLastPart += last[i] + " ";
                }
                lastPart = newLastPart.trim() + "...";
            }

            String textQuote = createTextQuote(phrases, firstPart, lastPart);

            Quote quote = new Quote();
            quote.setUri(uri + item.getNumber());
            quote.setQuote(textQuote);
            quotes.add(quote);
        }
        return quotes;
    }

    String createPartQuote(String content, String regexp, String text, String flag) {
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(content);

        if(matcher.find()) {
            text = matcher.group();
        }

        if(flag.equals("left")) {
            if (text.charAt(1) == ')' || text.charAt(1) == '»') {
                String temp = text.substring(2, text.length());
                if(content.length() - text.length() > 0) {
                    text = createPartQuote(content.substring(0, (content.length() - text.length()) + 2), forCreateLeftPartQuote, text, "left");
                }
                text += temp;
            }
        }

        if(flag.equals("right") && content.length() > text.length()) {
            if (content.charAt(text.length()) == ')' || content.charAt(text.length()) == '»') {
                text += createPartQuote(content.substring(text.length(), content.length()), forCreateRightPartQuote, text, "right");
            }
        }
        return text;
    }

    private String createTextQuote(String[] phrases, String firstPart, String lastPart) {
        String textQuote = firstPart;
        for (int i = 1; i < phrases.length - 1; i++) {
            if(!textQuote.isEmpty()) {
                textQuote += textQuote.charAt(textQuote.length()-1) == '-' ? "<strong>" + phrases[i].trim() : " <strong>" + phrases[i].trim();
            }
            else {
                textQuote = "<strong>" + phrases[i].trim();
            }
        }

        if(!textQuote.isEmpty() && textQuote.charAt(textQuote.length()-1) == '-') {
            textQuote += lastPart;
        }
        else {
            textQuote += " " + lastPart;
        }
        return textQuote.trim();
    }

    private String createRegExp(List<String> queries) {
        String reg = "";
        for(String s : queries) {
            reg += s + "|";
        }
        return reg.substring(0, reg.length()-1);
    }
}


