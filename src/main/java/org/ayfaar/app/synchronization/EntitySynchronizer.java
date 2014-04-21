package org.ayfaar.app.synchronization;

import org.ayfaar.app.model.UID;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class EntitySynchronizer<E extends UID> {
    abstract void synchronize(E entity) throws Exception;

    protected Map<String, E> scheduled = new LinkedHashMap<String, E>();

    public void scheduleSync(E uid) {
        scheduled.put(uid.getUri(), uid);
    }

    public void syncScheduled() throws Exception {
        for (Map.Entry<String, E> entry : scheduled.entrySet()) {
            synchronize(entry.getValue());
        }
        scheduled.clear();
    }
}
