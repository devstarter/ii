package org.ayfaar.app.dao;

import org.ayfaar.app.model.Item;

public interface ItemDao extends BasicCrudDao<Item> {
    Item getByNumber(String number);

//    List<Item> find(String query);
}
