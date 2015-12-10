package org.ayfaar.app.contents;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Этот класс - базовый для классов, которыми заполняем БД.
 *  Это набросок для того, что будет сделано в дальнейшем.
 */
@Data
@NoArgsConstructor
public class ItemBook {
    private String description;
    private String name;
    private String code;
    private String uri;

    private boolean chapter;
    private boolean paragraph;
    private boolean section;
    private boolean root;
    private boolean cikl;

//    том.раздел.глава.параграф
    private String tom;
    private String razdel;
    private String glava;
    private String paragraf;

    public ItemBook(String description) {
        this.description = description;
    }

    public int parseRomanianNumber(String str){
        String RimSym[] ={"I", "V", "X"};
        int[] ArabSym ={1,5,10};
        int out = 0;
        if(str.length() > 2){
            if(str.substring(0,2).equals("IV"))
                out = 4 + parseRomanianNumber(str.substring(2));
            else if(str.substring(0,2).equals("IX"))
                out = 9 + parseRomanianNumber(str.substring(2));
            else
                out = parseRomanianNumber(str.substring(1));
        } else {
            for (int i = 0; i < RimSym.length; i++) {
                if(RimSym[i].equals(str)){
                    out = ArabSym[i];
                    break;
                }
            }
        }
        return out;
    }
}

