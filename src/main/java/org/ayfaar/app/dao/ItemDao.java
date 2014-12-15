package org.ayfaar.app.dao;

import org.ayfaar.app.model.Item;

import java.util.List;

public interface ItemDao extends BasicCrudDao<Item> {
    Item getByNumber(String number);

//    List<Item> find(String query);

    public List<Item> findInContent(List<String> aliases);
}
