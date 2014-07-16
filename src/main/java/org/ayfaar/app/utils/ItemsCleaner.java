package org.ayfaar.app.utils;

/**
 * Очистка пунктов от сносок, названий Глав и Разделов
 *
 * issue2 https://github.com/devstarter/ii/issues/2
 * issue3 https://github.com/devstarter/ii/issues/3
 */
public class ItemsCleaner {

    public static String clean(String value) {
        String newContext = "";

        if(value == null || value.length() == 0) {
            return null;
        }

        newContext = cleanChapter(value);
        newContext = cleanSection(newContext);
        //System.out.println(newContext);
        return newContext;
    }

    private static String cleanChapter(String value) {
        String[] str = value.split("\nГлава");
        return cleaner(str);
    }

    private static String cleanSection(String value) {
        String[] str = value.split("\nРаздел|РАЗДЕЛ");
        return cleaner(str);
    }

    private static String cleaner(String[] content) {
        StringBuilder stringBuilder = new StringBuilder();
        if(content.length > 1) {
            for(int i = 0; i < content.length - 1; i++) {
                stringBuilder.append(content[i]);
            }
            return stringBuilder.toString().trim();
        } else {
            return content[0].trim();
        }
    }
}
