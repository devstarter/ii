package org.ayfaar.app.utils;


import org.apache.commons.lang.NotImplementedException;

/**
 * Очистка пунктов от сносок, названий Глав и Разделов
 *
 * https://github.com/devstarter/ii/issues?milestone=2
 */
public class ItemsHelper {

    public static final String QUESTION = "ВОПРОС.";

    public static String clean(String value) {
        String newContext = "";

        if(value == null) {
            return null;
        }

        newContext = cleanChapter(value);
        newContext = cleanSection(newContext);
        return newContext;
    }

    private static String cleanChapter(String value) {
        String[] str = value.split("\nГлава");
        return cleaner(str);
    }

    private static String cleanSection(String value) {
        String[] str = value.split("\nРаздел|(([XVI]+\\s+)?РАЗДЕЛ)");
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
                String questionPart = lastPartOfContent.substring(lastPartOfContent.indexOf(needSave));
                stringBuilder.append(questionPart);
            }
            return stringBuilder.toString().trim();
        } else {
            return content[0].trim();
        }
    }

    /**
     * Этот метод разделяет текст и вопрос
     *
     * @param text текст абзаца с вопросом
     *
     * @return массив 1 элемент это текст без вопроса, 2 - это вопрос со словом ВОПРОС.
     */
    public static String[] removeQuestion(String text) {
        throw new NotImplementedException("Issue13");
    }

    /**
     * Конкатенирует вопрос и ответ
     *
     * @param question
     * @param text
     * @return конкатенированный вариант
     */
    public static String addQuestion(String question, String text) {
        throw new NotImplementedException("Issue13");
    }
}
