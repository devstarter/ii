package org.ayfaar.app.dao;

import org.ayfaar.app.model.UIDTest;

import java.util.List;

public interface UIDDao extends BasicCrudDao<UIDTest> {
    public List<UIDTest> getAll();
    public void removeByUri(String uri);
}

