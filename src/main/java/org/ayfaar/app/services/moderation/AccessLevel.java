package org.ayfaar.app.services.moderation;

public enum AccessLevel {
    ROLE_ADMIN(0), ROLE_EDITOR(1), ROLE_AUTHENTICATED(2), ROLE_ANONYMOUS(999);

    private int precedence;

    AccessLevel(int precedence) {
        this.precedence = precedence;
    }

    public boolean accept(AccessLevel requiredAccessLevel) {
        return precedence <= requiredAccessLevel.precedence;
    }
}
