package org.ayfaar.app.dao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BasicCrudDao<E> {
    E save(E entity);
    E merge(E entity);

    /**
     * Get entity by ID
     * @param id
     * @return
     */
    @Nullable E get(@NotNull Serializable id);

    /**
     * Get Entity with property equals value.
     * Example:
     * itemDao.get("number", "1.0001");
     * means get item with property number equals "1.0001"
     * SQL: ... WHERE content = "1.0001"
     *
     * Property can be nested like "person.contact.email"
     *
     * Return only one entity, if found more throw exception.
     *
     * @param property field of entity
     * @param value
     * @return one entity
     */
    @Nullable E get(String property, @NotNull Object value);

    /**
     * Same as get(property, value), but return list of entity
     *
     * @see org.ayfaar.app.dao.BasicCrudDao#get(String, Object)
     *
     * @param property field of entity
     * @param value
     * @return list of Entity
     */
    List<E> getList(String property, @NotNull Object value);

    /**
     * SQL: ... WHERE <property> LIKE <expression>
     *
     * expression depends from MatchMode
     *
     * @see org.hibernate.criterion.MatchMode
     *
     * @param property field of entity
     * @param value
     * @param matchMode
     * @return
     */
    List<E> getLike(String property, @NotNull String value, MatchMode matchMode);

    List<E> getLike(String property, @NotNull String value, MatchMode matchMode, int limit);

    List<E> getLike(String property, @NotNull List<String> values, MatchMode matchMode);

    /**
     * SQL: ...WHERE <property> REGEXP <regexp>
     *
     * @see org.ayfaar.app.dao.impl.AbstractHibernateDAO#regexp(String, String)
     * @see http://www.mysql.ru/docs/man/Regexp.html
     *
     * @param property
     * @param regexp
     * @return list of Entity
     */
    @SuppressWarnings("JavadocReference")
    List<E> getByRegexp(String property, String regexp);
    List<E> getByRegexp(String property, String regexp, int limit);

    /**
     * Remove entity from database by ID
     * @param id
     */
    void remove(@NotNull Serializable id);

    @NotNull
    List<E> getPage(int skip, int pageSize);
    @NotNull
    List<E> getPage(int skip, int pageSize, String sortField, String sortDirection);
    @NotNull
    List<E> getPage(int skip, int pageSize, String sortField, String sortDirection, List<String> aliases, List<Criterion> criterions);

    @NotNull
    default List<E> getPage(Pageable pageable) {
        final Sort sort = pageable.getSort();
        Optional<Sort.Order> order = Optional.ofNullable(sort.iterator().hasNext() ? sort.iterator().next() : null);
        return getPage(
                pageable.getOffset(),
                pageable.getPageSize(),
                order.orElseGet(() -> null).getProperty(),
                order.orElseGet(() -> null).getDirection().name());
    }

    @NotNull
    Long getCount();
    @NotNull
    Long getCount(List<String> aliases, List<Criterion> criterions);

    /**
     * Return all records for this Entity
     * @return
     */
    @NotNull
    List<E> getAll();

    @Nullable
    E getRandom(@Nullable Criterion restriction);

    List<E> getFor(String entity, Serializable id);

    E getOneFor(String entity, Serializable id);

    List<E> getByExample(E o);
/*
    List<E> getAudit(Serializable id);

    List<E> getAllAudit();*/

    E initialize(E detachedParent, String fieldName);
}
