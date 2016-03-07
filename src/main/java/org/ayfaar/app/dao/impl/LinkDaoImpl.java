package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.UID;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ayfaar.app.model.LinkType.ABBREVIATION;
import static org.ayfaar.app.model.LinkType.ALIAS;
import static org.ayfaar.app.model.LinkType.CODE;
import static org.hibernate.criterion.Restrictions.*;

@SuppressWarnings("unchecked")
@Repository
public class LinkDaoImpl extends AbstractHibernateDAO<Link> implements LinkDao {
    public LinkDaoImpl() {
        super(Link.class);
    }

    @Override
    public List<Link> getAliases(String uri) {
        return criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(eq("uid1.uri", uri))
                .add(or(in("type", new Object[] {ALIAS, ABBREVIATION, CODE})))
                .list();
    }

    @Override
    public UID getPrimeForAlias(String uri) {
        Link link = (Link) criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(eq("uid2.uri", uri))
                .add(or(in("type", new Object[] {ALIAS, ABBREVIATION, CODE})))
                .uniqueResult();
        if (link != null) {
            return link.getUid1();
        } else {
            return null;
        }
    }

    @Override
    public List<Link> getRelated(String uri) {
        return getRelatedCriteria(uri).list();
    }

    private Criteria getRelatedCriteria(String uri) {
        return criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(or(eq("uid1.uri", uri), eq("uid2.uri", uri)))
                .add(or(in("type", new LinkType[]{ALIAS, CODE}), isNull("type")))
//                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.desc("weight"));
    }

    @Override
    public List<Link> getAllLinks(String uri) {
        return criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(or(eq("uid1.uri", uri), eq("uid2.uri", uri)))
                .list();
    }

    @Override
    public Link getForAbbreviationOrAliasOrCode(String uri) {
        return (Link) criteria()
                .createAlias("uid2", "uid2")
                .add(eq("uid2.uri", uri))
                .add(in("type", new LinkType[]{ABBREVIATION, ALIAS, CODE}))
                .uniqueResult();
    }

    @Override
    public List<Link> getRelatedWithQuote(String uri) {
        return getRelatedCriteria(uri)
                .add(isNotNull("quote"))
                .list();
    }

    @Override
    public List<Link> getByUris(String uri1, String uri2) {
        return criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(
                    or(
                        and(eq("uid1.uri", uri1), eq("uid2.uri", uri2)),
                        and(eq("uid1.uri", uri2), eq("uid2.uri", uri1))
                    )
                )
                .list();
    }

    @Override
    public List<Link> get(UID uid1, UID uid2) {
        return criteria()
                .add(
                    or(
                        and(eq("uid1", uid1), eq("uid2", uid2)),
                        and(eq("uid1", uid2), eq("uid2", uid1))
                    )
                )
                .list();
    }

    @Override
    public List<Link> getAllSynonyms() {
        return  criteria().add(in("type", new LinkType[]{ABBREVIATION, ALIAS, CODE})).list();
    }
}
