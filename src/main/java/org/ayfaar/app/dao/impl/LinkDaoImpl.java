package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.*;

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
                .add(eq("uid1.id", uri))
                .add(eq("type", Link.ALIAS))
                .list();
    }

    @Override
    public Link getPrimeForAlias(String uri) {
        return (Link) criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(eq("uid2.id", uri))
                .add(eq("type", Link.ALIAS))
                .uniqueResult();
    }

    @Override
    public List<Link> getRelated(String uri) {
        return criteria()
                .createAlias("uid1", "uid1")
                .createAlias("uid2", "uid2")
                .add(or(eq("uid1.id", uri), eq("uid2.id", uri)))
                .add(or(ne("type", Link.ALIAS), isNull("type")))
//                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.desc("weight"))
                .list();
    }

    @Override
    public Link getForAbbreviation(String uri) {
        return (Link) criteria()
                .createAlias("uid2", "uid2")
                .add(eq("uid2.id", uri))
                .add(eq("type", Link.ABBREVIATION))
                .uniqueResult();
    }
}
