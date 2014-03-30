package org.ayfaar.app.model;

import org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@RevisionEntity
@Table
public class Revision extends DefaultTrackingModifiedEntitiesRevisionEntity {
    private boolean wikiSynchronized;

    public boolean isWikiSynchronized() {
        return wikiSynchronized;
    }

    public void setWikiSynchronized(boolean wikiSynchronized) {
        this.wikiSynchronized = wikiSynchronized;
    }
}
