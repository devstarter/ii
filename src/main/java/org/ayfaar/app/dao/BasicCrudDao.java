package org.ayfaar.app.dao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public interface BasicCrudDao<E> {
    E save(E entity);
    E merge(E entity);

    @Nullable E get(@NotNull Serializable id);
    @Nullable E get(String property, @NotNull Object o);
    @Nullable List<E> getList(String property, @NotNull Object o);

    @Nullable
    List<E> getLike(String property, @NotNull String value, MatchMode matchMode);

    void remove(@NotNull Serializable id);

    @NotNull
    List<E> getPage(int skip, int pageSize);
    @NotNull
    List<E> getPage(int skip, int pageSize, String sortField, String sortDirection);
    @NotNull
    List<E> getPage(int skip, int pageSize, String sortField, String sortDirection, List<String> aliases, List<Criterion> criterions);

    @NotNull
    Long getCount();
    @NotNull
    Long getCount(List<String> aliases, List<Criterion> criterions);

    @NotNull
    List<E> getAll();

    @Nullable
    E getRandom(@Nullable Criterion restriction);

    List<E> getFor(String entity, Serializable id);

    E getOneFor(String entity, Serializable id);

    List<E> getByExample(E o);

    List<E> getAudit(Serializable id);

    List<E> getAllAudit();

    E initialize(E detachedParent, String fieldName);
}
