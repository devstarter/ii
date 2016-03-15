package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.utils.Content;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.apache.commons.beanutils.PropertyUtils.setProperty;
import static org.ayfaar.app.utils.EntityUtils.getPrimaryKeyFiledName;
import static org.ayfaar.app.utils.EntityUtils.getPrimaryKeyValue;
import static org.ayfaar.app.utils.RegExpUtils.W;
import static org.hibernate.criterion.Restrictions.*;

@SuppressWarnings("unchecked")
@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class CommonDaoImpl implements CommonDao {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public <E> List<E> getAll(Class<E> clazz) {
        return list(sessionFactory.getCurrentSession().createCriteria(clazz));
    }

    @Override
    public <E> E getRandom(Class<E> clazz) {
        return (E) sessionFactory.getCurrentSession().createCriteria(clazz)
             .add(Restrictions.sqlRestriction("1=1 order by rand()"))
             .setMaxResults(1)
             .list().get(0);
    }

    @Nullable
    @Override
    public <E> E get(Class<E> clazz, Serializable id) {
        return (E) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public <E> E get(Class<E> clazz, String property, Object value) {
        return (E) sessionFactory.getCurrentSession()
                .createCriteria(clazz).add(eq(property, value))
                .uniqueResult();
    }

    @Override
    public <E> List<E> getList(Class<E> clazz, String property, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz).add(value == null ? isNull(property) : eq(property, value))
                .list();
    }

    @Override
    public <E> List<E> getFor(Class<E> clazz, String entity, Serializable id) {
        return list(sessionFactory.getCurrentSession().createCriteria(clazz)
                .createAlias(entity, entity)
                .add(eq(entity+".id", id)));
    }

    @Override
    public <E> E getSingleFor(Class<E> clazz, String entity, Serializable id) {
        return (E) sessionFactory.getCurrentSession().createCriteria(clazz)
                .createAlias(entity, entity)
                .add(eq(entity + ".id", id))
                .uniqueResult();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <E> E save(Class<E> clazz, E entity) {
        try {
            Object id = getPrimaryKeyValue(entity);
            if (id != null && id.equals(new Integer(0))) {
                setProperty(entity, getPrimaryKeyFiledName(clazz), null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <E> E save(E entity) {
        return save((Class<E>) entity.getClass(), entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void remove(Class<?> entityClass, Serializable id) {
        Object entity = get(entityClass, id);
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void remove(Object entity) {
        remove(entity.getClass(), (Serializable) getPrimaryKeyValue(entity));
    }

    @Override
    public <E> E getByCode(Class<E> className, String code) {
        return (E) sessionFactory.getCurrentSession()
                .createCriteria(className)
                .add(eq("code", code))
                .uniqueResult();
    }

    protected <E> List<E> list(Criteria criteria) {
        return new ArrayList<E>(new LinkedHashSet<E>(criteria.list())); // privent duplications
    }

    @Override
    public <E> E initialize(Class<E> className, E detachedParent,String fieldName) {
        // ...open a hibernate session...
        // reattaches parent to session
        E reattachedParent = (E) sessionFactory.getCurrentSession().merge(detachedParent);

        // get the field from the entity and initialize it
        try {
            Field fieldToInitialize = detachedParent.getClass().getDeclaredField(fieldName);
            fieldToInitialize.setAccessible(true);
            Object objectToInitialize = fieldToInitialize.get(reattachedParent);
            Hibernate.initialize(objectToInitialize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return reattachedParent;
    }

    @Override
    public List<Content> findInAllContent(String query, Integer start, Integer limit) {
        /* Notes:
         *  - case insensitive searching
         *  - need brackets around query for case query="word1|word2|...|wordN"
         *  - it is important to LIMIT every part of SELECT query {why ?}
         */
//        query = query.replaceAll("\\s", "\\\\s");
        query = "("+query+")";
        String where = " WHERE LOWER(content) REGEXP '("+ W +"|^)" + query + W + "'";
        String itemQuery = "SELECT uri, NULL, content FROM item"+ where;
//                + " order by SUBSTRING(number FROM SUBSTRING_INDEX(number, \".\", 0))";
        String articleQuery = "SELECT uri, name, content FROM article"+where;
        List<Object[]> list = sessionFactory.getCurrentSession().createSQLQuery(
                itemQuery + " UNION " + articleQuery)
                .setFirstResult(start)
                .setMaxResults(limit)
                .list();
        List<Content> contents = new ArrayList<Content>();
        for (Object[] o : list) {
            contents.add(new Content((String) o[0], (String) o[1], (String) o[2]));
        }
        return contents;
    }

    @Override
    public List<Content> findInAllContent(List<String> aliases, Integer start, Integer limit) {
        String where = " WHERE ";
        for (String alias : aliases) {
            if (alias.length() < 4) {
                for (char endChar : new char[]{'?', '!', ',', '.', ' ', '"', ';', ':', ')'}) {
                    where += " content like '%" + alias + endChar + "%' OR";
                }
            } else {
                where += " content like '%" + alias + "%' OR";
            }
        }
        where = where.substring(0, where.length() - 2);

        String itemQuery = "SELECT uri, NULL, content FROM item"+ where;
//                + " order by SUBSTRING(number FROM SUBSTRING_INDEX(number, \".\", 0))";
        String articleQuery = "SELECT uri, name, content FROM article"+where;
        List<Object[]> list = sessionFactory.getCurrentSession().createSQLQuery(
                itemQuery + " UNION " + articleQuery)
                .setFirstResult(start)
                .setMaxResults(limit)
                .list();
        List<Content> contents = new ArrayList<Content>();
        for (Object[] o : list) {
            contents.add(new Content((String) o[0], (String) o[1], (String) o[2]));
        }
        return contents;
    }

    @Override
    public <E> List<E> getLike(Class<E> className, String field, String value, Integer limit) {
        return list(sessionFactory.getCurrentSession()
                .createCriteria(className)
                .add(ilike(field, value))
                .setMaxResults(limit)
        );
    }

    @Override
    public <E> List<E> getOrdered(Class<E> clazz, String field, boolean ascending, int limit) {
        return list(sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(ascending ? Order.asc(field) : Order.desc(field))
                .setMaxResults(limit)
        );
    }
}
