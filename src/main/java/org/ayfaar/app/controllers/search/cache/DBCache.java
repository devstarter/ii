package org.ayfaar.app.controllers.search.cache;


public interface DBCache {
    public void save(JsonEntity json);
    public boolean has(String name);
    public JsonEntity get(String name);
    public void clean();
}
