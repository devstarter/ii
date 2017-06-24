package org.ayfaar.app.utils.exceptions;

public enum Exceptions {

    TOPIC_NOT_FOUND("Topic for {} not found"),
    ITEM_NOT_FOUND("Item {} not found"),
    CONFIRMATION_REQUIRED("Action {} require confirmation"),
    LINK_NOT_FOUND("Link for {} and {} not found"),
    USER_NOT_FOUND("User with email {} not found"),
    ROLE_NOT_FOUND("Role for {} not found");


    private String message;

    Exceptions(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }
}
