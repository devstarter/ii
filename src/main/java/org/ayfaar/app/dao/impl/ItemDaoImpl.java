package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.like;

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

    /*@Override
    public List<Item> find(String query) {
	    query = query.toLowerCase().replaceAll("\\*", "["+w+"]*");
        return sqlQuery("SELECT * FROM item WHERE LOWER(content) REGEXP '("+ W +"|^)"
                    + query
                    + W + "'")
            .addEntity(Item.class)
            .setMaxResults(20)
            .list();
    }*/

    @Override
    public List<Item> findInContent(List<String> aliases) {
        Criteria criteria = criteria();
        Disjunction disjunction = Restrictions.disjunction();

        for (String alias : aliases) {
            disjunction.add(like("content", alias, MatchMode.ANYWHERE));
        }
        criteria.add(disjunction);

        return criteria.list();
    }
}
