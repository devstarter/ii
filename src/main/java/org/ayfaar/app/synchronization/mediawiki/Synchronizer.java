package org.ayfaar.app.synchronization.mediawiki;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Revision;
import org.hibernate.envers.RevisionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Synchronizer {
    @Autowired CommonDao commonDao;
    @Autowired CategorySync categorySync;
    @Autowired LinkDao linkDao;

//    @PostConstruct
    public void synchronize() throws Exception {
        List<Revision> newRevisions = commonDao.getList(Revision.class, "wikiSynchronized", false);

        List<Object> entities = new ArrayList<Object>();

        for (Revision revision : newRevisions) {
            entities.addAll(commonDao.findAuditEntities(revision.getId(), RevisionType.ADD));
//            entities.addAll(auditReader.findEntities(revision.getId(), RevisionType.MOD));
        }

        for (Object entity : entities) {
            if (entity instanceof Category) {
                Category cat = (Category) entity;
                if (cat.getStart() != null && cat.getEnd() != null) {
                    categorySync.synchronize(cat);
                }
            }
        }

    }

}
