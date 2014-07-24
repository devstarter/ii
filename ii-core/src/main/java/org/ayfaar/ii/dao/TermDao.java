package org.ayfaar.ii.dao;

import org.ayfaar.ii.model.Term;

import java.util.List;

public interface TermDao extends BasicCrudDao<Term> {
    Term getByName(String name);

    List<Term> getLike(String field, String value);

    List<Term> getGreaterThan(String field, Object value);
}
