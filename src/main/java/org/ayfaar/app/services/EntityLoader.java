package org.ayfaar.app.services;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.SoftCache;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class EntityLoader {
    private final CommonDao commonDao;

    private SoftCache<String, UID> cache = new SoftCache<>();

    @Inject
    public EntityLoader(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    public <E extends UID> E get(String uri) {
        //noinspection unchecked
        return (E) cache.getOrCreate(uri, () -> commonDao.getOpt(UriGenerator.getClassByUri(uri), uri)
                .orElseThrow(() -> new RuntimeException("Entity not found, uri: " + uri)));
    }

    public void clear() {
        cache.clear();
    }
}
