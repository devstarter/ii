package org.ayfaar.app.dao;

import org.ayfaar.app.model.Item;

import java.util.List;

public interface SearchDao extends BasicCrudDao<Item> {
    public List<Item> searchInDb(String query, int skipResults, int maxResults, String fromItemNumber);
    public List<Item> searchInDb(List<String> words, int skipResults, int maxResults, String fromItemNumber);
    public List<Item> findInItems(List<String> aliases, int skip, int limit);
}
