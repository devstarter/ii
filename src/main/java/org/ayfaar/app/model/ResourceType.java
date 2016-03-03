package org.ayfaar.app.model;

import org.ayfaar.app.utils.CharEnum;

public enum ResourceType implements CharEnum {
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
