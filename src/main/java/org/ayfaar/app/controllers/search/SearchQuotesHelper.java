package org.ayfaar.app.controllers.search;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.RegExpUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.join;

@Component
public class SearchQuotesHelper {
    public static final int MAX_WORDS_ON_BOUNDARIES = 50;
    private static final String forCreateLeftPartQuote = "([^\\.\\?!]*)([\\.\\?!]*)(\\.|\\?|\\!)(\\)|\\»)";
    private static final String forCreateRightPartQuote = "(\\)|\\»)([^\\.\\?!]*)([\\.\\?!]*)";

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        List<Quote> quotes = new ArrayList<Quote>();
        String forLeftPart = "([\\.\\?!]*)([^\\.\\?!]*)(<strong>)";
        String forRightPart = "(<strong>)([^\\.\\?!]*)([\\.\\?!]*)";
        String regexp = join(allPossibleSearchQueries, "|");

        for (Item item : foundedItems) {
            String content = "";
            Pattern pattern = Pattern.compile("(^" + regexp + ")|(" + RegExpUtils.W + "+" + regexp + RegExpUtils.W +
                    "+)|(" + regexp + "$)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(item.getContent());

            if (matcher.find()) {
                content = item.getContent().replaceAll("(?iu)\\b(" + regexp + ")\\b", "<strong>$1</strong>");
            }

            String[] phrases = content.split("<strong>");
            String leftPart = getPartQuote(phrases[0] + "<strong>", forLeftPart, "", "left");

            if(leftPart.charAt(0) == '.' || leftPart.charAt(0) == '?' || leftPart.charAt(0) == '!') {
                leftPart = leftPart.substring(1, leftPart.length());
                leftPart = leftPart.trim();
            }
            String[] first = leftPart.split(" ");

            String rightPart = getPartQuote("<strong>" + phrases[phrases.length-1], forRightPart, "", "right");
            String[] last = rightPart.split(" ");

            leftPart = cutSentence(leftPart, first.length - (MAX_WORDS_ON_BOUNDARIES + 1), first.length, "left", first);
            rightPart = cutSentence(rightPart, 0, MAX_WORDS_ON_BOUNDARIES + 1, "right", last);

            String textQuote = createTextQuote(phrases, leftPart, rightPart);

            Quote quote = new Quote();
            quote.setNumber(item.getNumber());
            quote.setQuote(textQuote);
            quotes.add(quote);
        }
        return quotes;
    }

    String getPartQuote(String content, String regexp, String text, String flag) {
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(content);

        if(matcher.find()) {
            text = matcher.group();
        }

        if(flag.equals("left") && !text.isEmpty()) {
            if (text.charAt(1) == ')' || text.charAt(1) == '»') {
                String temp = text.substring(2, text.length());

                if(content.length() - text.length() > 0) {
                    text = getPartQuote(content.substring(0, (content.length() - text.length()) + 2),
                            forCreateLeftPartQuote, text.substring(2, text.length()), "left");

                    int offset = 0;
                    if(content.contains(text)) {
                        offset = content.indexOf(text);
                    }
                    text += content.substring(text.length() + offset, content.length() - temp.length());
                }
                text += temp;
            }
        }

        if(flag.equals("right") && content.length() > text.length()) {
            if (content.charAt(text.length()) == ')' || content.charAt(text.length()) == '»') {
                text += getPartQuote(content.substring(text.length(), content.length()),
                        forCreateRightPartQuote, text, "right");
            }
        }
        return text;
    }

    private String cutSentence(String text, int startIndex, int endIndex, String flag, String[] words) {
        String partText = "";
        if(words.length > MAX_WORDS_ON_BOUNDARIES + 1) {
            for(int i = startIndex; i < endIndex; i++) {
                partText += words[i] + " ";
            }
            if (flag.equals("left")) {
                partText = partText.trim();
                text = "..." + partText.substring(0, partText.length() - 8).trim();
            }
            if(flag.equals("right")) {
                text = partText.trim() + "...";
            }
        }
        else if(words.length <= MAX_WORDS_ON_BOUNDARIES + 1 && flag.equals("left")) {
            text = text.substring(0, text.length() - 8).trim();
        }
        return text;
    }

    private String createTextQuote(String[] phrases, String firstPart, String lastPart) {
        String textQuote = firstPart;
        for (int i = 1; i < phrases.length - 1; i++) {
            textQuote += (textQuote.isEmpty() || textQuote.charAt(textQuote.length()-1) == '-' ? "" : " ")
                + "<strong>" + phrases[i].trim();
        }

        if(!textQuote.isEmpty() && textQuote.charAt(textQuote.length()-1) == '-') {
            textQuote += lastPart;
        }
        else {
            textQuote += " " + lastPart;
        }
        return textQuote.trim();
    }
}


