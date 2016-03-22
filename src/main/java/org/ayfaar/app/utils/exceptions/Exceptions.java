package org.ayfaar.app.utils.exceptions;

public enum Exceptions {

    TOPIC_NOT_FOUND("Topic for %s not found"),
    ITEM_NOT_FOUND("Item %s not found");
    //OTHER_CODE("Description");


    private String message;

    Exceptions(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }
}
