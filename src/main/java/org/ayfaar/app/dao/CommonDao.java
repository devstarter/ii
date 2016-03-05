package org.ayfaar.app.dao;

import org.ayfaar.app.utils.Content;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public interface CommonDao {
    <E> List<E> getAll(Class<E> clazz);

    @Nullable
    <E> E get(Class<E> clazz, Serializable id);
    <E> E get(Class<E> clazz, String property, Object value);

    <E> E save(Class<E> entityClass, E entity);
    <E> E save(E entity);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    void remove(Class<?> entityClass, Serializable id);
    void remove(Object entity);

    <E> E getByCode(Class<E> entity, String code);

    <E> List<E> getList(Class<E> clazz, String property, Object value);

    <E> List<E> getFor(Class<E> clazz, String entity, Serializable id);

    <E> E getSingleFor(Class<E> clazz, String entity, Serializable id);

    <E> E getRandom(Class<E> clazz);

    <E> E initialize(Class<E> className, E detachedParent, String fieldName);

//    List<Content> findInAllContent(String query);

    List<Content> findInAllContent(String query, Integer start, Integer pageSize);

    List<Content> findInAllContent(List<String> aliases, Integer start, Integer pageSize);

    <E> List<E> getLike(Class<E> className, String field, String value, Integer limit);

    <E> List<E> getOrdered(Class<E> clazz, String field, boolean ascending, int limit);
}
