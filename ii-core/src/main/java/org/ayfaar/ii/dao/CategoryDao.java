package org.ayfaar.ii.dao;

import org.ayfaar.ii.model.Category;

import java.util.List;

public interface CategoryDao extends BasicCrudDao<Category> {
    List<Category> getTopLevel();

    Category getForItem(String itemUri);

//    List<Category> getChildren(String uri);
}
