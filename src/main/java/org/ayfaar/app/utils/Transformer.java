package org.ayfaar.app.utils;

public interface Transformer<E, T> {
    public T transform(E value);
}
