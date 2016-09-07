package org.ayfaar.app.utils.contents;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContentsUtils {

    public static String filterLengthWordsAfter(String paragraph, String search, int countWordsBeforeAndAfter){
        String wholeFind = "";
        String searchResult = null;
        String str = paragraph;
        String find = search;

        //check if not the full text
        Pattern pattern = Pattern.compile("\\S*" + find + "\\S*",Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            wholeFind = matcher.group();
        }

        //create minimal string
        int countWords = countWordsBeforeAndAfter;
        String[] sp = str.split(" +"); // "+" for multiple spaces
        List<String> strings = Arrays.asList(sp);
        String[] findStringArr = {};
        if (wholeFind.equals("")) findStringArr = find.split(" ");
        else findStringArr = wholeFind.split(" ");

        int lengthFindStringArr = findStringArr.length;
        int lengthParagraph = sp.length;
        String firstPosition = findStringArr[0];
        String lastPosition = findStringArr[lengthFindStringArr - 1];
        int iLast = strings.indexOf(lastPosition);

        for (int i = 0; i < lengthParagraph; i++) {
            String addFirstDots = "";
            if (sp[i].equals(firstPosition) && sp[i + lengthFindStringArr-1].equals(lastPosition)) {

                String after = "";
                for (int j = 1; j <= countWords; j++) {
                    if(iLast+j < lengthParagraph) after += " " + sp[iLast+j];
                }

                after = after.replaceAll("[-+.^:,]$","");
                if(!after.equals(" ") && iLast + countWords < lengthParagraph) after += "...";
                if (!firstPosition.equals(sp[0])) addFirstDots  = "...";
                searchResult = addFirstDots + wholeFind + after;
            }
        }

        return searchResult == null ? "" : searchResult;
    }
}

