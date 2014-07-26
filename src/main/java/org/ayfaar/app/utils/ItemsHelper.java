package org.ayfaar.app.utils;


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

        if (null == text) {
            return null;
        }

        if (text.isEmpty()){
            return new String[]{""};
        }

        String[] resultOfRemoveQuestion = new String[2];
        int indexOfQuestion = text.lastIndexOf(QUESTION);

        if (-1 == indexOfQuestion){
            resultOfRemoveQuestion[0] = text;
        }
        else {
            if (indexOfQuestion == 0){
                resultOfRemoveQuestion[1] = text;
                resultOfRemoveQuestion[0] = "";
            }
            else {
                resultOfRemoveQuestion[0] = text.substring(0, indexOfQuestion - 1)
                        .replaceAll("\\r\\n|\\r|\\n", "")
                        .trim();
                resultOfRemoveQuestion[1] = text.substring(indexOfQuestion);
            }

        }

        return  resultOfRemoveQuestion;
    }

    /**
     * Конкатенирует вопрос и ответ
     *
     * @param question
     * @param text
     * @return конкатенированный вариант
     */
    public static String addQuestion(String question, String text) {

        if (question == null || text == null){
            return null;
        }

        if (question.isEmpty() && text.isEmpty()){
            return "";
        }

        return question + "\r\n" + text;
    }
}
