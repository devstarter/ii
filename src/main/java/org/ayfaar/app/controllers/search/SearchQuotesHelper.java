package org.ayfaar.app.controllers.search;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.RegExpUtils;
import org.ayfaar.app.utils.TermsMarker;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.join;

@Component
public class SearchQuotesHelper {
    protected static int MAX_WORDS_ON_BOUNDARIES = 50;
    private static final String forCreateLeftPartQuote = "([^\\.\\?!]*)([\\.\\?!]*)(\\.|\\?|\\!)(\\)|\\»| -| –)";
    private static final String forCreateRightPartQuote = "(\\)|\\»| -| –)[^\\.\\?!]*[\\.\\?!]*";
    private static final List<String> brackets = Arrays.asList(".)", "!)", "?)", ".»", "!»", "?»");
    private static final List<String> directSpeech = Arrays.asList(". –", "! –", "? –", ". -", "! -", "? -");
    private static final List<Character> openQuoteBracketHyphen = Arrays.asList('«', '(', '-');
    private static final List<String> punctual = Arrays.asList(".", "?", "!", ":", ";");

    @Inject TermsMarker termsMarker;

    public List<Quote> createQuotes(List<Item> foundedItems, List<String> allPossibleSearchQueries) {
        List<Quote> quotes = new ArrayList<Quote>();
        String forLeftPart = "([\\.\\?!]*)([^\\.\\?!]*)(<strong>)";
        String forRightPart = "<strong>[^\\.\\?!]+[\\.\\?!]*</strong>[^\\.\\?!]*[\\.\\?!]*";
        String regexp = join(allPossibleSearchQueries, "|");
        regexp = regexp.replace("*", "["+RegExpUtils.w+"]*");


        for (Item item : foundedItems) {
            String content = "";
            Pattern pattern = Pattern.compile("(^" + regexp + ")|(" + RegExpUtils.W + "+" + regexp + RegExpUtils.W +
                    "+)|(" + regexp + "$)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(item.getTaggedContent());

            if (matcher.find()) {
                if(punctual.contains(regexp.substring(regexp.length()-1))) {
                    String lastCharacter = regexp.substring(regexp.length()-1);
                    String newRegexp = lastCharacter.equals(".") || lastCharacter.equals("?") ?
                            regexp.substring(0, regexp.length()-1) + "\\" + lastCharacter : regexp;
                    content = item.getTaggedContent().replaceAll("(?iu)(" + newRegexp + ")", "<strong>$1</strong>");
                }
                else {
                    content = item.getTaggedContent().replaceAll("(?iu)\\b(" + regexp + ")\\b", "<strong>$1</strong>");
                }
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
            if (textQuote.isEmpty()) {
                textQuote = item.getTaggedContent();
            } /*else {
                textQuote = termsMarker.mark(textQuote);
            }*/
            Quote quote = new Quote();
            quote.setNumber(item.getNumber());
            textQuote = textQuote.replaceAll("^[^<]+</term>\\s*", "...");
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

        if(flag.equals("left")) {
            if(brackets.contains(text.substring(0, 2))) {
                text = skipBracketOrDirectSpeech(content, text, text.substring(2, text.length()), 2);
            }
            else if(directSpeech.contains(text.substring(0, 3))) {
                text = skipBracketOrDirectSpeech(content, text, text.substring(3, text.length()), 3);
            }
        }

        if(flag.equals("right") && content.length() > text.length()) {
            String sub = content.substring(text.length(), content.length());
            if(sub.startsWith(")") || sub.startsWith("»") || isHyphen(sub.substring(0,2))) {
                text += getPartQuote(content.substring(text.length(), content.length()),
                        forCreateRightPartQuote, text, "right");
            }
        }
        return text;
    }

    private String skipBracketOrDirectSpeech(String content, String text, String temp, int skip) {
        if(content.length() - text.length() > 0) {
            text = getPartQuote(content.substring(0, (content.length() - text.length()) + skip),
                    forCreateLeftPartQuote, text.substring(2, text.length()), "left");

            int offset = 0;
            if(content.contains(text)) {
                offset = content.indexOf(text);
            }
            text += content.substring(text.length() + offset, content.length() - temp.length());
        }
        text += temp;
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
            textQuote += (textQuote.isEmpty() ||
                    openQuoteBracketHyphen.contains(textQuote.charAt(textQuote.length()-1)) ? "" : " ")
                    + "<strong>" + phrases[i].trim();
        }

        if(!textQuote.isEmpty() && openQuoteBracketHyphen.contains(textQuote.charAt(textQuote.length()-1))) {
            textQuote += lastPart;
        }
        else {
            textQuote += " " + lastPart;
        }
        return textQuote.trim();
    }

    private boolean isHyphen(String text) {
        char hyphen = text.charAt(1);
        return (int)hyphen == 45 ? true : (int)hyphen == 8211 ? true : false;
    }
}


