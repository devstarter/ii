package org.ayfaar.app.dao;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.NewAliasesMap;


import java.util.List;

public interface TermDao extends BasicCrudDao<Term> {
    Term getByName(String name);

    List<Term> getLike(String field, String value);

    List<Term> getGreaterThan(String field, Object value);

    List<NewAliasesMap.TermInfo> getAllTermInfo();
}
