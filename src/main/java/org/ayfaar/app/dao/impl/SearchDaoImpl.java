package org.ayfaar.app.dao.impl;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hibernate.criterion.Restrictions.like;

@Repository
public class SearchDaoImpl extends AbstractHibernateDAO<Item> implements SearchDao {

    public SearchDaoImpl() {
        super(Item.class);
    }

    public List<Item> searchInDb(String query, int skipResults, int maxResults, String fromItemNumber) {
        return searchInDb(asList(query), skipResults, maxResults, fromItemNumber);
    }

    public List<Item> searchInDb(List<String> words, int skipResults, int maxResults, String fromItemNumber) {

        // 4.3. В результате нужно знать есть ли ещё результаты поиска для следующей страницы
        findInItems(words, skipResults, maxResults, fromItemNumber);
        throw new NotImplementedException();
    }

    public List<Item> findInItems(List<String> aliases, int skip, int limit, String fromItemNumber) {
        final String columnName = "number";
        String op = ">=";
        Criteria criteria = criteria();
        Disjunction disjunction = Restrictions.disjunction();

        if(fromItemNumber != null) {
            criteria.add(new SimpleExpression(columnName, fromItemNumber, op) {
                @Override
                public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
                    StringBuilder fragment = new StringBuilder();
                    fragment.append("CAST(");
                    fragment.append(columnName);
                    fragment.append(" as DECIMAL(10,6))");
                    fragment.append(getOp()).append("?");

                    return fragment.toString();
                }
            });
        }

        for (String alias : aliases) {
            for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')', '»', '-'}) {
                for (char startChar : new char[]{'-', ' ', '(', '«'})  {
                    disjunction.add(like("content", startChar + alias + endChar, MatchMode.ANYWHERE));
                }
                disjunction.add(like("content", alias + endChar, MatchMode.ANYWHERE));
            }
        }

        criteria.add(disjunction).setMaxResults(limit).setFirstResult(skip);
        criteria.addOrder(Order.asc("orderIndex"));

        return criteria.list();
    }
}


