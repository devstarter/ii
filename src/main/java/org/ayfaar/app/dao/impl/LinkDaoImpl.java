package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.UID;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

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
                .add(or(eq("type", Link.ALIAS),eq("type", Link.ABBREVIATION)))
                .list();
    }

    @Override
    public UID getPrimeForAlias(String uri) {
        Link link = (Link) criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(eq("uid2.uri", uri))
                .add(or(eq("type", Link.ALIAS),eq("type", Link.ABBREVIATION)))
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
                .add(or(in("type", new Byte[]{Link.ALIAS, Link.CODE}), isNull("type")))
//                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.desc("weight"));
    }

    @Override
    public Link getForAbbreviation(String uri) {
        return (Link) criteria()
                .createAlias("uid2", "uid2")
                .add(eq("uid2.uri", uri))
                .add(eq("type", Link.ABBREVIATION))
                .uniqueResult();
    }

    @Override
    public List<Link> getRelatedWithQuote(String uri) {
        return getRelatedCriteria(uri)
                .add(isNotNull("quote"))
                .list();
    }
}
