package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.BasicCrudDao;
import org.ayfaar.app.model.Item;
import org.dozer.DozerBeanMapper;
import org.hibernate.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.criterion.*;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.metadata.ClassMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.setProperty;
import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.like;
import static org.springframework.util.Assert.notNull;

@SuppressWarnings("unchecked")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class AbstractHibernateDAO<E> implements BasicCrudDao<E> {

    protected final Class<E> entityClass;
    @Autowired SessionFactory sessionFactory;
    @Autowired ImprovedNamingStrategy namingStrategy;

    public AbstractHibernateDAO(Class<E> entityClass) {
        notNull(entityClass, "entityClass must not be null");
        this.entityClass = entityClass;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public E save(E entity) {
        try {
            ClassMetadata metadata = sessionFactory.getClassMetadata(entityClass);
            Object id = getProperty(entity, metadata.getIdentifierPropertyName());
            if (id != null && id.equals(new Integer(0))) {
                setProperty(entity, metadata.getIdentifierPropertyName(), null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        currentSession().saveOrUpdate(entity);
        return entity;
    }

    @Nullable
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public E merge(@Nullable E entity) {
        return (E) currentSession().merge(entity);
    }

    protected Criteria criteria() {
        return currentSession().createCriteria(entityClass);
    }

    protected Query query(String hql) {
        return currentSession().createQuery(hql);
    }

    protected SQLQuery sqlQuery(String hql) {
        return currentSession().createSQLQuery(hql);
    }

    protected Query queryByName(String queryName) {
        return currentSession().getNamedQuery(queryName);
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    protected List<E> all() {
        return list(criteria());
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

        /* === BEGIN GENERICS SUPPRESSION WRAPPERS === */

    protected List<E> list(Criteria criteria) {
        return list(criteria, true);
    }

    @SuppressWarnings("unchecked")
    protected List<E> list(Criteria criteria, boolean cache) {
        criteria.setCacheable(cache);
        return new ArrayList<E>(new LinkedHashSet<E>(criteria.list())); // privent duplications
    }

    protected List<E> list(Query query) {
        return list(query, true);
    }

    @SuppressWarnings("unchecked")
    protected List<E> list(Query query, boolean cache) {
        query.setCacheable(cache);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected E uniqueResult(Criteria criteria) {
        return (E) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    protected E uniqueResult(Query query) {
        return (E) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public E get(Serializable id) {
        return (E) currentSession().get(entityClass, id);
    }

    @Nullable
    @Override
    public E get(String property, @NotNull Object o) {
        Criteria criteria = criteria();
        if (property.indexOf(".") > 0) {
            String[] aliases = property.split("\\.");
            String aProperty = "";
            for (int i=0; i < aliases.length-1; i++) {
                if (!aProperty.isEmpty()) {
                    aProperty += ".";
                }
                aProperty += aliases[i];
                String tmp = aProperty.replace(".", "_");
                criteria.createAlias(aProperty, tmp);
                aProperty = tmp;
            }
            property = aProperty+"."+aliases[aliases.length-1];
        }

        return (E) criteria.add(eq(property, o))
                .uniqueResult();
    }

    @Nullable
    @Override
    public List<E> getList(String property, @NotNull Object o) {
        return criteria()
                .add(eq(property, o))
                .list();
    }

    @Nullable
    @Override
    public List<E> getLike(String property, @NotNull String value, MatchMode matchMode) {
        return criteria()
                .add(like(property, value, matchMode))
                .list();
    }

    @SuppressWarnings("unchecked")
    protected E load(Serializable id) {
        return (E) currentSession().load(entityClass, id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void remove(Serializable id) {
        E entity = (E) currentSession().get(entityClass, id);
        currentSession().delete(entity);
    }

    public List<E> getPage(int skip, int pageSize) {
        return list(criteria().setFirstResult(skip).setMaxResults(pageSize));
    }

    public List<E> getPage(int skip, int pageSize, String sortField, String sortDirection) {
        Criteria criteria = currentSession().createCriteria(entityClass)
                .setFirstResult(skip)
                .setMaxResults(pageSize);

        if (sortField != null && !sortField.isEmpty()) {
            criteria.addOrder("asc".equals(sortDirection) ? Order.asc(sortField) : Order.desc(sortField));
        }

        return list(criteria);
    }

    @NotNull
    @Override
    public List<E> getPage(int skip, int pageSize, String sortField, String sortDirection,
                           List<String> aliases, List<Criterion> criterions) {
        Criteria criteria = currentSession().createCriteria(entityClass)
                .setFirstResult(skip)
                .setMaxResults(pageSize);

        for (String alias : aliases) {
            criteria.createAlias(alias, alias);
        }
        if (sortField != null && !sortField.isEmpty()) {
            criteria.addOrder("asc".equals(sortDirection) ? Order.asc(sortField) : Order.desc(sortField));
        }
        for (Criterion item : criterions) {
            criteria.add(item);
        }

        return list(criteria);
    }

    public Long getCount() {
        return (Long) currentSession().createQuery("select count (*) from "+entityClass.getName())
                .uniqueResult();
    }

    @NotNull
    @Override
    public Long getCount(List<String> aliases, List<Criterion> criterions) {
        Criteria criteria = criteria();

        for (String alias : aliases) {
            criteria.createAlias(alias, alias);
        }
        for (Criterion item : criterions) {
            criteria.add(item);
        }
        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    public Long getCount(Set<Criterion> filter) {
        Criteria criteria = currentSession().createCriteria(entityClass)
                .setProjection(rowCount());
        for (Criterion item : filter) {
            criteria.add(item);
        }
        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<E> getAll() {
        return criteria()
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Nullable
    public E getRandom(@Nullable Criterion restriction) {
        Criteria criteria = criteria();
        criteria.add(restriction);
        criteria.setProjection(rowCount());
        int count = ((Number) criteria.uniqueResult()).intValue();
        if (0 != count) {
            int index = new Random().nextInt(count);
            criteria = criteria();
            criteria.add(restriction);
            return (E) criteria.setFirstResult(index).setMaxResults(1).uniqueResult();
        }
        return null;
    }

    @Override
    @Fetch(FetchMode.SELECT)
    public List<E> getFor(String entity, Serializable id) {
        return list(criteria()
                .createAlias(entity, entity)
                .add(eq(entity+".id", id)));
    }

    @Override
    public E getOneFor(String entity, Serializable id) {
        return (E) criteria()
                .createAlias(entity, entity)
                .add(eq(entity+".id", id))
                .uniqueResult();
    }

    @Override
    public List<E> getByExample(E o) {
        return list(criteria().add(Example.create(o)));
    }

    @Override
    public List<E> getAudit(Serializable id) {
        AuditReader reader = AuditReaderFactory.get(currentSession());

        AuditQuery query = reader.createQuery().forRevisionsOfEntity(entityClass, false, true);

        //if you have added your username field in the RevisionEntity/_revision_info you should use this:
        //query.add(AuditEntity.revisionProperty("username").eq(userName));

        query.add(AuditEntity.id().eq(id));

        List<E> result = new ArrayList<E>();
        List<Object[]> audits = query.getResultList();

        DozerBeanMapper mapper = new DozerBeanMapper();

        for (Object[] objects : audits) {
            result.add(mapper.map(objects[0], entityClass));
        }

        return result;
    }

    @Override
    public List<E> getAllAudit() {
        AuditReader reader = AuditReaderFactory.get(currentSession());

        AuditQuery query = reader.createQuery().forRevisionsOfEntity(entityClass, false, true);

        List<E> result = new ArrayList<E>();
        List<Object[]> audits = query.getResultList();

        DozerBeanMapper mapper = new DozerBeanMapper();

        for (Object[] objects : audits) {
            result.add(mapper.map(objects[0], entityClass));
        }

        return result;
    }

    @Override
    public E initialize(E detachedParent, String fieldName) {
        // ...open a hibernate session...
        // reattaches parent to session
        E reattachedParent = (E) currentSession().merge(detachedParent);

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
}