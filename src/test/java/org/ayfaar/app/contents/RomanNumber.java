package org.ayfaar.app.contents;

import org.junit.Test;

/**
 * This class is made for working
 * with Roman numerals
 * @author Ruav
 */
public class RomanNumber {

//    public static void main(String[] args) {
//        System.out.println(convertToRomanNumber(14));
//        System.out.println(convertToRomanNumber(9));
//        System.out.println(convertToRomanNumber(1));
//        System.out.println(convertToRomanNumber(4));
//        System.out.println(convertToRomanNumber(23));
//        System.out.println(convertToRomanNumber(49));
//    }

    /**
     * Function for converting from
     * Roman number to Arabic number.
     * @param str String with roman
     * numerals, which we need convert.
     * @return int Converted value from
     * roman numerals to arabic numerals.
     *
     */

    public static int parseRomanNumber(String str){
        String RomanSymbols[] ={"I", "V", "X"};
        int[] ArabicSymbols ={1, 5, 10};
        int out = 0;
        if(str.length() > 1){
            switch(str.toUpperCase().substring(0,2)){
                case "IV":
                    out = 4 + parseRomanNumber(str.substring(2));
                    break;
                case "IX":
                    out = 9 + parseRomanNumber(str.substring(2));
                    break;
                default:
                    out = parseRomanNumber(str.substring(0,1)) + parseRomanNumber(str.substring(1));
            }
//            if(str.substring(0,2).equals("IV"))
//                out = 4 + parseRomanNumber(str.substring(2));
//            else if(str.substring(0,2).equals("IX"))
//                out = 9 + parseRomanNumber(str.substring(2));
//            else
//                out = parseRomanNumber(str.substring(0,1)) + parseRomanNumber(str.substring(1));
        } else {
            for (int i = 0; i < RomanSymbols.length; i++) {
                if(RomanSymbols[i].equals(str)){
                    out = ArabicSymbols[i];
                    break;
                }
            }
        }
        return out;
    }

    /**
     * Function for converting from
     * Arabic number to Roman number.
     * @param num integer with arabic
     * numerals, which we need convert.
     * @return String Converted value from
     * arabic numerals to roman numerals.
     *
     */

    public static String convertToRomanNumber(int num){
        String RomanSymbols[] ={"I", "IV", "V", "IX", "X"};
        int[] ArabicSymbols ={1, 4, 5, 9, 10};
        int[] numbers = {0, 0, 0, 0, 0};
        int temp;
        String out = "";
        temp = num;

        for(int i=ArabicSymbols.length-1; i>=0;i--) {
            if (num >= ArabicSymbols[i]) {
                numbers[i] = temp / ArabicSymbols[i];
                temp = temp % ArabicSymbols[i];
            }
        }
        for(int i=ArabicSymbols.length-1;i>=0;i--){
            for (int j = 0; j < numbers[i]; j++) {
                out += RomanSymbols[i];
            }
        }

        return out;
    }
}
