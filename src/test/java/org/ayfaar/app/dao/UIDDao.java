package org.ayfaar.app.dao;

import java.util.List;

public interface UIDDao {
    public List<String> getAll();
    public void removeByUri(String uri);
}

