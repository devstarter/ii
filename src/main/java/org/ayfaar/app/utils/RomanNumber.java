package org.ayfaar.app.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is made for working
 * with Roman numerals
 *
 * @author Ruav
 */
public class RomanNumber {

    /**
     * Function for converting from
     * Roman number to Arabic number.
     *
     * @param in String with roman
     *           numerals, which we need convert.
     * @return int Converted value from
     * roman numerals to arabic numerals.
     */

    public static int parse(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Expect not null string");
        }
        if (str.isEmpty()) {
            throw new NullPointerException("String is empty");
        }
        String in = str.toUpperCase().replaceAll(" ", "");

        Pattern pattern = Pattern.compile("[\\sIVX]");
        Matcher matcher = pattern.matcher(in);
        if (!matcher.find()) {
            throw new NumberFormatException("Wrong string");
        }
        String romanSymbols[] = {"I", "V", "X"};
        int[] arabicSymbols = {1, 5, 10};

        int out = 0;
        int length = in.length();
        if (length > 1) {
            switch (in.toUpperCase().substring(0, 2)) {
                case "IV":
                    out = 4 + ((length > 2) ? parse(in.substring(2)) : 0);
                    break;
                case "IX":
                    out = 9 + ((length > 2) ? parse(in.substring(2)) : 0);
                    break;
                default:
                    out = parse(in.substring(0, 1)) + parse(in.substring(1));
            }
//            if(str.substring(0,2).equals("IV"))
//                out = 4 + parseRomanNumber(str.substring(2));
//            else if(str.substring(0,2).equals("IX"))
//                out = 9 + parseRomanNumber(str.substring(2));
//            else
//                out = parseRomanNumber(str.substring(0,1)) + parseRomanNumber(str.substring(1));
        } else {
            for (int i = 0; i < romanSymbols.length; i++) {
                if (romanSymbols[i].equals(in)) {
                    out = arabicSymbols[i];
                    break;
                }
            }
        }
        return out;
    }

    /**
     * Function for converting from
     * Arabic number to Roman number.
     *
     * @param num integer with arabic
     *            numerals, which we need convert.
     * @return String Converted value from
     * arabic numerals to roman numerals.
     */

    public static String convertToRomanNumber(Integer num) {
        String romanSymbols[] = {"X", "IX", "V", "IV", "I"};
        int[] arabicSymbols = {10, 9, 5, 4, 1};
        int[] numbers = {0, 0, 0, 0, 0};
        int MAXIMUM_KOEFF = 5; // берется из расчета, что для 50 идет уже символ L, а у нас он не заведен
        if (num == null) {
            throw new NullPointerException("Nullpointer Exception");
        }
        if (num <= 0) {
            throw new NumberFormatException("Less then or equals zero");
        }
        if (num > arabicSymbols[0] * MAXIMUM_KOEFF) {
            throw new NumberFormatException("More then maximum for this function");
        }
        int temp;
        String out = "";
        temp = num;

        for (int i = 0; i < arabicSymbols.length; i++) {
            if (num >= arabicSymbols[i]) {
                numbers[i] = temp / arabicSymbols[i];
                temp = temp % arabicSymbols[i];
            }
        }
        for (int i = 0; i < arabicSymbols.length; i++) {
            for (int j = 0; j < numbers[i]; j++) {
                out += romanSymbols[i];
            }
        }

        return out;
    }
}
