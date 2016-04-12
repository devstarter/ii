package org.ayfaar.app.dao;

import org.ayfaar.app.model.ItemsRange;

import java.util.List;

public interface ItemsRangeDao extends BasicCrudDao<ItemsRange> {
    List<ItemsRange> getWithCategories();
}
