package org.ayfaar.app.model;

import org.ayfaar.app.utils.hibernate.ValueEnum;

public enum LinkType implements ValueEnum {
    /**
     * Синомим, первым следует указывать более точное понятие или код
     */
    ALIAS(1),
    /**
     * Аббревиатура или сокращение, первым указывают полное значение
     */
    ABBREVIATION(2),
    /**
     * Ссылка на код понятия
     * Первый понятие, второй код
     */
    CODE(4),
    /**
     * Ссылка на дочерний объет
     * Первый родитель, второй потомок
     */
    CHILD(5),
    /**
     * Перевод
     */
    TRANSLATION(6);

    protected Byte value;

    LinkType(int value) {
        this.value = (byte) value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    public static LinkType getEnum(String value) {
        return getEnum(Byte.valueOf(value));
    }

    public static LinkType getEnum(Byte value) {
        for (LinkType status : values()) {
            if (status.getValue().equals(value)) return status;
        }
        throw new RuntimeException("No LinkType for "+value);
    }

    public boolean isChild() {
        return this == CHILD;
    }
}
