package org.ayfaar.app.utils;

import java.util.Comparator;

public class AdvanceComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if (o2 == null || o1 == null) {
            return 0;
        }

        int lengthFirstStr = o1.length();
        int lengthSecondStr = o2.length();

        int index1 = 0;
        int index2 = 0;

        //пока не достигнуты концы строк
        while (index1 < lengthFirstStr && index2 < lengthSecondStr) {
            char ch1 = o1.charAt(index1);
            char ch2 = o2.charAt(index2);

            char[] space1 = new char[lengthFirstStr];
            char[] space2 = new char[lengthSecondStr];

            int loc1 = 0;
            int loc2 = 0;

            //строку o1 считываем посимвольно в space1 до тех пор, пока не будет достигнут конец строки o1
            //или пока не закончится непрерывная последовательность из символов (не цифр) или последовательность из цифр
            do {
                space1[loc1++] = ch1;
                index1++;

                if (index1 < lengthFirstStr) {
                    ch1 = o1.charAt(index1);
                } else {
                    break;
                }
            } while (Character.isDigit(ch1) == Character.isDigit(space1[0]));

            //строку o2 считываем посимвольно в space2 до тех пор, пока не будет достигнут конец строки o2
            //или пока не закончится непрерывная последовательность из символов (не цифр) или последовательность из цифр
            do {
                space2[loc2++] = ch2;
                index2++;

                if (index2 < lengthSecondStr) {
                    ch2 = o2.charAt(index2);
                } else {
                    break;
                }
            } while (Character.isDigit(ch2) == Character.isDigit(space2[0]));

            //Из массивов символов получаем сроки для дальнейшего сравнения.
            //Это буду последовательности либо из цифр, либо из нецифровых символов.
            String str1 = new String(space1);
            String str2 = new String(space2);

            int result;

            //если нецифровые строки равны и следующие за ними символы в строках o1 и o2 цифры
            if (str1.substring(0, index1).equalsIgnoreCase(str2.substring(0, index2)) && Character.isDigit(o1.charAt(index1)) && Character.isDigit(o2.charAt(index2))) {

                //найдем индекс последнего цифрового символа в последовательности в строке o1, увеличенный на 1
                int index11 = index1;
                while (Character.isDigit(o1.charAt(++index11))) {

                }

                //найдем индекс последнего цифрового символа в последовательности в строке o2, увеличенный на 1
                int index21 = index2;
                while (Character.isDigit(o2.charAt(++index21))){

                }

                //получим числовые значения в строках и сравним их
                Integer firstNumberToCompare = Integer.parseInt(o1.substring(index1, index11));
                Integer secondNumberToCompare = Integer.parseInt(o2.substring(index2, index21));
                result = firstNumberToCompare.compareTo(secondNumberToCompare);
            } else {
                result = str1.compareTo(str2);
            }

            //Если сравниваемые последовательности не равны, то вернем результат сравнения.
            //В противном случае продолжим сравнивать o1 и o2 в новом цикле.
            if (result != 0) {
                return result;
            }
        }
        return lengthFirstStr - lengthSecondStr;
    }
}
