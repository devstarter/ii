package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.utils.Content;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
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
import static org.ayfaar.app.utils.RegExpUtils.w;
import static org.hibernate.criterion.Restrictions.eq;

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
                .createCriteria(clazz).add(Restrictions.eq(property, value))
                .uniqueResult();
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
                .add(Restrictions.eq("code", code))
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
    public List<Content> findInAllContent(String query) {
        // case insensitive !!
        query = query.toLowerCase();
        String itemQuery = "SELECT uri, NULL, content FROM item WHERE LOWER(content) REGEXP '("+ W +"|^)" + query + W + "'";
        String articleQuery = "SELECT uri, name, content FROM article WHERE LOWER(content) REGEXP '("+ W +"|^)" + query + W + "'";
        List<Object[]> list = sessionFactory.getCurrentSession().createSQLQuery(
                itemQuery + " UNION " + articleQuery)
                .setMaxResults(20)
                .list();
        List<Content> contents = new ArrayList<Content>();
        for (Object[] o : list) {
            contents.add(new Content((String) o[0], (String) o[1], (String) o[2]));
        }
        return contents;
    }
    /* // such sql-query spend about 45 seconds
     SELECT uri, NULL, content FROM item
     WHERE LOWER(content) REGEXP '([^a-za-zа-яа-я0-9ёё]|^)(фокуса|фокусам|фокусами|фокусах|фокусе|фокусов|фокусом|фокусу|фокусы)[^a-za-zа-яа-я0-9ёё]'
     UNION
     SELECT uri, name, content FROM article
     WHERE LOWER(content) REGEXP '([^a-za-zа-яа-я0-9ёё]|^)(фокуса|фокусам|фокусами|фокусах|фокусе|фокусов|фокусом|фокусу|фокусы)[^a-za-zа-яа-я0-9ёё]' limit 20
     */
}