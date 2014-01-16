package org.ayfaar.app.dao;

import org.ayfaar.app.model.Link;

import java.util.List;

public interface LinkDao extends BasicCrudDao<Link> {
    List<Link> getAliases(String uri);

    Link getPrimeForAlias(String uri);

    List<Link> getRelated(String uri);
}
