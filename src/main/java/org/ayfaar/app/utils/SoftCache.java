package org.ayfaar.app.utils;


import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class SoftCache<K, V> {
    private LinkedHashMap<K, SoftReference<V>> map = new LinkedHashMap<>();

    public V getOrCreate(K key, Supplier<V> creator) {
        V value = null;
        if (map.containsKey(key)) {
            value = map.get(key).get();
        }
        if (value == null) {
            value = creator.get();
            map.put(key, new SoftReference<V>(value));
        }
        return value;
    }
}
