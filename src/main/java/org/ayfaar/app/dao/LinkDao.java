package org.ayfaar.app.dao;

import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.UID;

import java.util.List;

public interface LinkDao extends BasicCrudDao<Link> {
    List<Link> getAliases(String uri);

    UID getPrimeForAlias(String uri);

    List<Link> getRelated(String uri);

    List<Link> getAllLinks(String uri);

    Link getForAbbreviationOrAliasOrCode(String uri);

    List<Link> getRelatedWithQuote(String uri);

    List<Link> getByUris(String uri1, String uri2);

    List<Link> get(UID uid1, UID uid2);

    List<Link> getAllSynonyms();
}
