package org.ayfaar.app.dao.impl;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.controllers.search.SearchFilter;
import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.hibernate.criterion.MatchMode;
import org.jetbrains.annotations.NotNull;
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

    public List<Item> searchInDb(String query, int skipResults, int maxResults, SearchFilter filter) {
        return searchInDb(asList(query), skipResults, maxResults, filter);
    }

    public List<Item> searchInDb(List<String> words, int skipResults, int maxResults, SearchFilter filter) {
        // 4.1. Результат должен быть отсортирован:
        // Сначала самые ранние пункты
        // 4.2. Если filter заполнен то нужно учесть стартовый и конечный  абзаци
        // 4.3. В результате нужно знать есть ли ещё результаты поиска для следующей страницы

        //getByRegexp("content", createRegexp(words));
        /*for (String query : words) {
            getLike("content", query, MatchMode.ANYWHERE);
        }*/
        //findInAllContent("времени");
        throw new NotImplementedException();
    }


    @Override
    public List<Item> getLike(String property, @NotNull String value, MatchMode matchMode) {
        return criteria()
                // .ignoreCase() нет смысла использовать так как mysql итак не зависит от регистра
                // то есть нет необходимости оверайдить гктЛайк метод
                .add(like(property, value.toLowerCase(), matchMode).ignoreCase())
                .list();
    }

    /*public String createRegexp(List<String> words) {
        throw new NotImplementedException();
    }*/

    public List<Item> sort(List<Item> items) {
        Collections.sort(items);
        return items;
    }

    @Override
    public List<Item> findInItems(List<String> aliases, int limit) {
        String where = " WHERE ";

        for (String alias : aliases) {
            for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')', '»'}) {
                // а зачем тебе здесь SQL, чего не используешь getLike ?
                where += " content like '% " + alias + endChar + "%' OR" +
                        " content like '%-" + alias + endChar + "%' OR" +
                        " content like '%(" + alias + endChar + "%' OR" +
                        " content like '" + alias + endChar + "%' OR";

            }
            where += " content like '%«" + alias + "»%' OR" + " content like '%«" + alias + " %' OR";
        }

        where = where.substring(0, where.length() - 2);

        String itemQuery = "SELECT number, content FROM item"+ where;
        // можно заменить на List<Item> list = sessionFactory.getCurrentSession().createSQLQuery(itemQuery).addEntity(Item.class).list();
        List<Object[]> list = sessionFactory.getCurrentSession().createSQLQuery(itemQuery).list();
        //System.out.println("list = " + list.size());

        List<Item> items = new ArrayList<Item>();
        for (Object[] o : list) {
            items.add(new Item((String) o[0], (String) o[1]));
        }

        items = sort(items);
        /*for(Item i : items) {
            System.out.println("dao item = " + i.getNumber());
        }*/
        return items;
    }

    @Override
    public List<Item> findInItems2(List<String> aliases, int limit) {
        String where = " WHERE ";

        for (String alias : aliases) {
            where += " content like '% " + alias + "%' OR" + " content like '%-" + alias + "%' OR";
        }

        where = where.substring(0, where.length() - 2);

        String itemQuery = "SELECT number, content FROM item"+ where;
        List<Object[]> list = sessionFactory.getCurrentSession().createSQLQuery(itemQuery).list();
        //System.out.println("list = " + list.size());

        List<Item> items = new ArrayList<Item>();
        for (Object[] o : list) {
            items.add(new Item((String) o[0], (String) o[1]));
        }

        items = sort(items);
        /*for(Item i : items) {
            System.out.println("dao item = " + i.getNumber());
        }*/
        return items;
    }

    @Override
    public List<Item> getByRegexp(String property, String regexp) {
        List<Item> items = criteria().add(regexp(property, regexp)).list();

        items = sort(items);
        return items;
    }
}


