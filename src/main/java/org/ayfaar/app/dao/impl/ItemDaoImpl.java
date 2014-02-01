package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.sqlRestriction;

@SuppressWarnings("unchecked")
@Repository
public class ItemDaoImpl extends AbstractHibernateDAO<Item> implements ItemDao {
    public ItemDaoImpl() {
        super(Item.class);
    }

    @Override
    public Item getByNumber(String number) {
        return get("number", number);
    }

    @Override
    public List<Item> find(String query) {
        return criteria()
                .add(sqlRestriction("content REGEXP '[\\s\\(>«]" + query + "[»<\\*\\s,:\\.\\?!\\)\\-]'"))
                .setMaxResults(100)
                .list();
    }
}
