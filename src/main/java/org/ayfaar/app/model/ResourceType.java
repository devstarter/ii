package org.ayfaar.app.model;

import org.ayfaar.app.utils.hibernate.ValueEnum;

public enum ResourceType implements ValueEnum<Character> {
    ARTICLE('A');

    private final char code;

    ResourceType(char code) {
        this.code = code;
    }

    @Override
    public Character getValue() {
        return code;
    }
}
