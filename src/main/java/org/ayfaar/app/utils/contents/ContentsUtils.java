package org.ayfaar.app.utils.contents;

import org.springframework.stereotype.Component;

@Component
public class ContentsUtils {

    public static final String SENTENCE_ENDS = ".!?";

    public static String splitToSentence(String paragraph, String search){

        String p1 = paragraph;
        String findString = search.toLowerCase();
        String result = "";

        StringBuilder buf = new StringBuilder();

        for ( char c : p1.toCharArray() ) {
            if ( c == '\n' )
                c = ' ';
            buf.append(c);
            if ( SENTENCE_ENDS.indexOf(c) > -1 ) {
                if(buf.toString().toLowerCase().contains(findString)) result = buf.toString().trim();
                buf = new StringBuilder();
            }
        }
        return  result;
    }
}

