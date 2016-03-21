package org.ayfaar.app.utils.exceptions;

public enum Exceptions {

    TOPIC_NOT_FOUND("TOPIC_NOT_FOUND", "Topic was not found.");
    //TOPIC_NOT_FOUND2("TOPIC_NOT_FOUND", "Topic was not found.");


    private final String code;
    private final String message;

    Exceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }

    public String getMessage() { return message; }
}
