package org.ayfaar.app.model;

import org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity;

//@Entity
//@RevisionEntity
//@Table
public class Revision extends DefaultTrackingModifiedEntitiesRevisionEntity {
    private boolean wikiSynchronized;

    public boolean isWikiSynchronized() {
        return wikiSynchronized;
    }

    public void setWikiSynchronized(boolean wikiSynchronized) {
        this.wikiSynchronized = wikiSynchronized;
    }
}
