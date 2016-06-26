package org.ayfaar.app.services;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

@Service
public class EntityLoader {
    private final CommonDao commonDao;

    private Map<String, SoftReference<? extends UID>> cache = new HashMap<>();

    @Inject
    public EntityLoader(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    public <E extends UID> E get(String uri) {
        E entity = null;
        final Class<? extends UID> entityClass = UriGenerator.getClassByUri(uri);
        if (cache.containsKey(uri)) {
            entity = (E) cache.get(uri).get();
        }
        if (entity == null)
        entity = (E) commonDao.getOpt(entityClass, uri)
                .orElseThrow(() -> new RuntimeException("Entity not found, uri: " + uri));

        cache.put(uri, new SoftReference<>(entity));
        return entity;
    }
}
