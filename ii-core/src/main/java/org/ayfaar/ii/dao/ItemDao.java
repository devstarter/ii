package org.ayfaar.ii.dao;

import org.ayfaar.ii.model.Item;

import java.util.List;

public interface ItemDao extends BasicCrudDao<Item> {
    Item getByNumber(String number);

    List<Item> find(String query);
}
