package org.ayfaar.app.utils.contents;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContentsUtils {

    public static String filterWordsBeforeAndAfter(String paragraph, String search, int countWordsBeforeAndAfter){
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

        for (int i = 0; i < sp.length; i++) {
            if (sp[i].equals(findStringArr[0]) && sp[i + findStringArr.length-1].equals(findStringArr[findStringArr.length-1])) {

                String before = "";
                int iFirst = strings.indexOf(findStringArr[0]);
                for (int j = countWords; j > 0; j--) {
                    if(iFirst-j >= 0) before += sp[iFirst-j]+" ";
                }

                if(before.equals("") && findStringArr.length == 1) countWords *= 2; //Multiple words if "before" empty

                String after = "";
                int iLast = strings.indexOf(findStringArr[findStringArr.length-1]);
                for (int j = 1; j <= countWords; j++) {
                    if(iLast+j < sp.length) after += " " + sp[iLast+j];
                }

                before = before.replaceFirst("^[-+.^:,]","");
                after = after.replaceAll("[-+.^:,]$","");
                if(!before.equals(" ") && iFirst - countWords > 0) before = "..." + before;
                if(!after.equals(" ") && iLast + countWords < sp.length) after += "...";
                searchResult = before + wholeFind + after;
            }
        }

        return searchResult;
    }
}

