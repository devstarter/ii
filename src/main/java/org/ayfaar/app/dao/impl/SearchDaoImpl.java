package org.ayfaar.app.dao.impl;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
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
        // 4.1. Результат должен быть отсортирован:
        // Сначала самые ранние пункты
        // 4.2. Если filter заполнен то нужно учесть стартовый и конечный  абзаци
        // 4.3. В результате нужно знать есть ли ещё результаты поиска для следующей страницы

        //findInItems(words, 0, 20);
        //findInItems(words, 20, 20);
        throw new NotImplementedException();
    }

    public List<Item> findInItems(List<String> aliases, int skip, int limit) {
        Criteria criteria = criteria();
        Disjunction disjunction = Restrictions.disjunction();

        for (String alias : aliases) {
            for (char startChar : new char[]{'-', ' ', '(', '«'})  {
                for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')', '»'}) {
                    disjunction.add(like("content", startChar + alias + endChar, MatchMode.ANYWHERE));
                }
            }
            //иногда фраза которую мы ищем стоит в самом начале пердложения и перед ней нет ни пробела, ни других знаков
            for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')', '»'}) {
                disjunction.add(like("content", alias + endChar, MatchMode.ANYWHERE));
            }
        }
        criteria.add(disjunction).setMaxResults(limit).setFirstResult(skip);

        List<Item> sortedItems = new ArrayList<Item>(criteria.list());
        Collections.sort(sortedItems);
        return sortedItems;
    }
}


