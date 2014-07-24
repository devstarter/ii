package org.ayfaar.ii.dao;

import org.ayfaar.ii.model.Link;
import org.ayfaar.ii.model.UID;

import java.util.List;

public interface LinkDao extends BasicCrudDao<Link> {
    List<Link> getAliases(String uri);

    UID getPrimeForAlias(String uri);

    List<Link> getRelated(String uri);

    List<Link> getAllLinks(String uri);

    Link getForAbbreviationOrAliasOrCode(String uri);

    List<Link> getRelatedWithQuote(String uri);

    List<Link> getByUris(String uri1, String uri2);
}
