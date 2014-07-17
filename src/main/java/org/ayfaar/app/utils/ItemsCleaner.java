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

        if(value == null) {
            return null;
        }

        newContext = cleanChapter(value);
        newContext = cleanSection(newContext);
        newContext = cleanSectionByRomanLetters(newContext);
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

    private static String cleanSectionByRomanLetters(String value) {
        String[] str = value.split("[A-Z]+");
        return cleaner(str);
    }

    private static String cleaner(String[] content) {
        StringBuilder stringBuilder = new StringBuilder();
        String lastPartOfContent = content[content.length-1];
        String needSave = "ВОПРОС";

        if(content.length > 1) {
            for(int i = 0; i < content.length - 1; i++) {
                stringBuilder.append(content[i]);
            }
            if(lastPartOfContent.contains(needSave)) {
                String questionPart = lastPartOfContent.substring(lastPartOfContent.indexOf(needSave), lastPartOfContent.length());
                stringBuilder.append(questionPart);
            }
            return stringBuilder.toString().trim();
        } else {
            return content[0].trim();
        }
    }
}
