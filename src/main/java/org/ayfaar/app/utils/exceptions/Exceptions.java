package org.ayfaar.app.utils.exceptions;

public enum Exceptions {

    TOPIC_NOT_FOUND("Topic for {} not found"),
    ITEM_NOT_FOUND("Item {} not found"),
    CONFIRMATION_REQUIRED("Action {} require confirmation"),
    LINK_NOT_FOUND("Link for {} and {} not found"),
    EMAIL_NOT_FOUND("Email {} not found");
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
