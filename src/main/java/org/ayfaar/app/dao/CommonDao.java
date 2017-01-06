package org.ayfaar.app.dao;

import org.ayfaar.app.utils.Content;
import org.hibernate.Criteria;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface CommonDao {
    <E> List<E> getAll(Class<E> clazz);

    <E> Optional<E> getOpt(Class<E> clazz, Serializable id);
    @Nullable
    // use getOpt() instead
    <E> E get(Class<E> clazz, Serializable id);
    <E> E get(Class<E> clazz, String property, Object value);
    // use getOpt() instead
    <E> Optional<E> getOpt(Class<E> clazz, String property, Object value);

    <E> E save(Class<E> entityClass, E entity);
    <E> E save(E entity);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    void remove(Class<?> entityClass, Serializable id);
    void remove(Object entity);

    <E> List<E> getList(Class<E> clazz, String property, Object value);

    <E> List<E> getList(Class<E> clazz, String property, Object value, Pageable pageable);

    <E> List<E> getListWithout(Class<E> clazz, String property, Object value, Pageable pageable);

    <E> List<E> getFor(Class<E> clazz, String entity, Serializable id);

    <E> E getSingleFor(Class<E> clazz, String entity, Serializable id);

    <E> E initialize(Class<E> className, E detachedParent, String fieldName);

    @Deprecated
    List<Content> findInAllContent(String query, Integer start, Integer pageSize);

    @Deprecated
    List<Content> findInAllContent(List<String> aliases, Integer start, Integer pageSize);

    <E> List<E> getLike(Class<E> className, String field, String value, Integer limit);

    @NotNull
    <E> List<E> getPage(Class<E> entityClass, Pageable pageable);

    @NotNull
    <E> List<E> getPage(Class<E> entityClass, int skip, int pageSize, String sortField, String sortDirection);

    Criteria getCriteria(Class entityClass, Pageable pageable);
}
