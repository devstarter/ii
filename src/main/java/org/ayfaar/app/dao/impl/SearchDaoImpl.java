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

    public List<Item> findInItems(List<String> aliases, int skip, int limit, String fromItemNumber) {
        Criteria criteria = criteria();
        Disjunction disjunction = Restrictions.disjunction();

        if(fromItemNumber != null) {
            final String columnName = "number";
            String op = ">=";
            criteria.add(new SimpleExpression(columnName, fromItemNumber, op) {
                @Override
                public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
                    // можно переделать для сравнения с order_index
                    return String.format("CAST(%s as DECIMAL(10,6))%s?", columnName, getOp());
                }
            });
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
        criteria.addOrder(Order.asc("orderIndex"));

        return criteria.list();
    }
}


