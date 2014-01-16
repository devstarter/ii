package org.ayfaar.app.dao;

import org.ayfaar.app.model.Term;

public interface TermDao extends BasicCrudDao<Term> {
    Term getByName(String name);
}
