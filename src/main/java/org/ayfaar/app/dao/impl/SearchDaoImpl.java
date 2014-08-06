package org.ayfaar.app.dao.impl;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.controllers.search.SearchFilter;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hibernate.criterion.Restrictions.like;

@Repository
public class SearchDaoImpl extends AbstractHibernateDAO<Item> implements SearchDao {
    public SearchDaoImpl() {
        super(Item.class);
    }

    public List<Item> searchInDb(String query, int skipResults, int maxResults, SearchFilter filter) {
        return searchInDb(asList(query), skipResults, maxResults, filter);
    }

    public List<Item> searchInDb(List<String> words, int skipResults, int maxResults, SearchFilter filter) {
        // 4.1. Результат должен быть отсортирован:
        // Сначала самые ранние пункты
        // 4.2. Если filter заполнен то нужно учесть стартовый и конечный  абзаци
        // 4.3. В результате нужно знать есть ли ещё результаты поиска для следующей страницы

        //findInItems(words, 0, 20);
        //findInItems(words, 20, 20);
        throw new NotImplementedException();
    }

    // зачем отдельный метод для сортировки?
    public List<Item> sort(List<Item> items) {
        Collections.sort(items);
        return items;
    }

    public List<Item> findInItems(List<String> aliases, int skip, int limit) {
        Criteria criteria = criteria();
        Disjunction disjunction = Restrictions.disjunction();

        for (String alias : aliases) {
            for (char startChar : new char[]{'-', ' ', '(', '«'})  {
                for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')', '»'}) {
                    // если уж используешь дизьюнкцию то нет необходимости в or
                    disjunction.add(like("content", startChar + alias + endChar, MatchMode.ANYWHERE));
                }
            }
            // зачем второй цикл? напиши в коментариях, так как без углубления в логику не понятно
            for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')', '»'}/*дублирование списка знаков*/) {
                disjunction.add(like("content", alias + endChar, MatchMode.ANYWHERE));
            }
        }
        criteria.add(disjunction).setMaxResults(limit).setFirstResult(skip);
        return sort(criteria.list());
    }
}


