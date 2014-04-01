package org.ayfaar.app.synchronization;

public interface EntitySynchronizer<E> {
    void synchronize(E entity) throws Exception;
}
