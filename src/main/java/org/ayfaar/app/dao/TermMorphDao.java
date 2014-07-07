package org.ayfaar.app.dao;

import org.ayfaar.app.model.TermMorph;

import java.util.List;

public interface TermMorphDao extends BasicCrudDao<TermMorph> {
    TermMorph getByName(String name);
    List<String> getAllMorphs(String termMorph);
}