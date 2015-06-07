package org.ayfaar.app.dao;

import org.ayfaar.app.model.Item;

import java.util.List;


public interface ItemDao extends BasicCrudDao<Item> {
    Item getByNumber(String number);

    List<String> getAllNumbers();

    List<Item> getNext(String number, Integer more);

//    List<Item> find(String query);
}
