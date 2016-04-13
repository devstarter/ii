package org.ayfaar.app.services.moderation;

import one.util.streamex.StreamEx;

import java.util.Optional;

import static one.util.streamex.MoreCollectors.onlyOne;

public enum AccessLevel {
    ROLE_ADMIN(0), ROLE_EDITOR(1), ROLE_AUTHENTICATED(2), ROLE_ANONYMOUS(999);

    private int precedence;

    AccessLevel(int precedence) {
        this.precedence = precedence;
    }

    public static Optional<AccessLevel> fromPrecedence(int precedence) {
        return StreamEx.of(AccessLevel.values())
                .filter(accessLevel -> accessLevel.getPrecedence() == precedence)
                .collect(onlyOne());
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean accept(AccessLevel requiredAccessLevel) {
        return precedence <= requiredAccessLevel.precedence;
    }
}
