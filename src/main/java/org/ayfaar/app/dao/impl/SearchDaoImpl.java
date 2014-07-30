package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchDaoImpl extends AbstractHibernateDAO<Item> implements SearchDao {
    public SearchDaoImpl() {
        super(Item.class);
    }

    @Override
    public List<Item> getByRegexp(String property, String regexp) {
        return criteria()
                .add(regexp(property, regexp)).addOrder(Order.asc("number"))
                .list();
    }
}
