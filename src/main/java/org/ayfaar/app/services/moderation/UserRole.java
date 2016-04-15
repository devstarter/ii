package org.ayfaar.app.services.moderation;

import one.util.streamex.StreamEx;

import java.util.Optional;

import static one.util.streamex.MoreCollectors.onlyOne;

public enum UserRole {
    ROLE_ADMIN(0), ROLE_EDITOR(1), ROLE_AUTHENTICATED(2), ROLE_ANONYMOUS(999);

    private int precedence;

    UserRole(int precedence) {
        this.precedence = precedence;
    }

    public static Optional<UserRole> fromPrecedence(int precedence) {
        return StreamEx.of(UserRole.values())
                .filter(accessLevel -> accessLevel.getPrecedence() == precedence)
                .collect(onlyOne());
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean accept(UserRole requiredAccessLevel) {
        return precedence <= requiredAccessLevel.precedence;
    }
}
