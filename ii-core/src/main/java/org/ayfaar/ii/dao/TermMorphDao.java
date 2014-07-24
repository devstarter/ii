package org.ayfaar.ii.dao;

import org.ayfaar.ii.model.TermMorph;

import java.util.List;

public interface TermMorphDao extends BasicCrudDao<TermMorph> {
    TermMorph getByName(String name);
    List<String> getAllMorphs(String termMorph);
}