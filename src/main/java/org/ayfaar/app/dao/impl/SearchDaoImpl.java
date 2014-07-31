package org.ayfaar.app.dao.impl;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.controllers.search.SearchFilter;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Arrays.asList;

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

        getByRegexp("content", createRegexp(words));

        throw new NotImplementedException();
    }

    @Override
    public List<Item> getByRegexp(String property, String regexp) {
        return criteria()
                .add(regexp(property, regexp)).addOrder(Order.asc("number"))
                .list();
    }

    public String createRegexp(List<String> words) {
        throw new NotImplementedException();
    }
}
