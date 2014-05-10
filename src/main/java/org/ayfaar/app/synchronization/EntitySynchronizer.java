package org.ayfaar.app.synchronization;

import org.ayfaar.app.model.UID;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class EntitySynchronizer<E extends UID> {
    abstract void synchronize(E entity) throws Exception;

    protected Map<String, E> scheduled = new LinkedHashMap<String, E>();

    public void scheduleSync(E uid) {
        scheduled.put(uid.getUri(), uid);
    }

    public void syncScheduled() throws Exception {
        Set<Map.Entry<String, E>> syncPass = new HashSet<Map.Entry<String, E>>(scheduled.entrySet());
        scheduled.clear();
        for (Map.Entry<String, E> entry : syncPass) {
            synchronize(entry.getValue());
        }
    }
}
