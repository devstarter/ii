package org.ayfaar.app.services.moderation;

public enum AccessLevel {
    ADMIN(0), EDITOR(1);

    private int precedence;

    AccessLevel(int precedence) {
        this.precedence = precedence;
    }
}
