package org.ayfaar.app.utils;


public class QuestionMover {
    private static String needFind = "ВОПРОС";

    public static String extractQuestion(String value) {
        String textQuestion = "";

        if(value == null) {
            return null;
        }

        int index = value.indexOf(needFind);
        if(index >= 0) {
            textQuestion = value.substring(index);
        }

        return textQuestion;
    }

    public static String cleanQuestion(String value) {
        StringBuilder content = new StringBuilder();

        if(value == null) {
            return null;
        }

        String[] paragraphs = value.split(needFind);

        if(paragraphs.length > 1) {
            for(int i = 0; i < paragraphs.length - 1; i++) {
                content.append(paragraphs[i]);
            }
            return content.toString().trim();
        }
        else {
            return paragraphs[0].trim();
        }
    }
}
