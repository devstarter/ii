package org.ayfaar.app.services.moderation;

public enum AccessLevel {
    ROLE_ADMIN(0), ROLE_EDITOR(1);

    private int precedence;

    AccessLevel(int precedence) {
        this.precedence = precedence;
    }
}
