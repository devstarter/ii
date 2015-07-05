package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.like;

@Repository
public class SearchDaoImpl extends AbstractHibernateDAO<Item> implements SearchDao {
    private boolean hasMore = false;

    public SearchDaoImpl() {
        super(Item.class);
    }

    public boolean hasMoreItems() {
        return hasMore;
    }

    public List<Item> findInItems(List<String> aliases, int skip, int limit, String startFrom) {
        Criteria criteria = criteria();
        Disjunction disjunction = Restrictions.disjunction();

        if(startFrom != null && !startFrom.isEmpty() && !startFrom.equals("undefined")) {
            criteria.add(new SimpleExpression("orderIndex", Float.valueOf(startFrom), ">=") {
                @Override
                public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
                    return " order_index >= ?";
                }
            });
            criteria.addOrder(Order.asc("orderIndex"));
        }

        for (String alias : aliases) {
            for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')', '»', '-'}) {
                for (char startChar : new char[]{'-', ' ', '(', '«'})  {
                    disjunction.add(like("content", startChar + alias + endChar, MatchMode.ANYWHERE));
                }
                disjunction.add(like("content", alias + endChar, MatchMode.START));
            }
        }

        criteria.add(disjunction).setMaxResults(limit).setFirstResult(skip);

        return criteria.list();
    }
}


