package org.ayfaar.app.services.moderation;

public enum AccessLevel {
    ADMIN(0), EDITOR(1), AUTHENTICATED(2), ANONYMOUS(999);

    private int precedence;

    AccessLevel(int precedence) {
        this.precedence = precedence;
    }

    public boolean accept(AccessLevel requiredAccessLevel) {
        return precedence <= requiredAccessLevel.precedence;
    }
}
