package org.ayfaar.app.controllers.search;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class SearchCacheUnitTest {

    SearchCache cache;

    @Before
    public void setUp() {
        // todo создать клас имплиментирующий SearchCache
        //cache = new SearchCacheImpl();
    }

    @Test
    public void testGenerateKey() {
        String query = "q";
        Object key1 = cache.generateKey(query, 1, null);
        Object key2 = cache.generateKey(query, 1, null);
        assertEquals("Ключи для одинаковых аргументов одинаковы", key1, key2);
        key2 = cache.generateKey("q2", 1, null);
        assertNotEquals("Ключи для разных аргументов разные", key1, key2);
        key2 = cache.generateKey(query, 2, null);
        assertNotEquals("Ключи для разных аргументов разные", key1, key2);
        key2 = cache.generateKey(query, 1, "1");
        assertNotEquals("Ключи для разных аргументов разные", key1, key2);
    }

    @Test
    public void test() {
        String key = "1";
        SearchResultPage page = new SearchResultPage();

        assertFalse(cache.has(key));
        assertNull(cache.get(key));

        cache.put(key, page);
        assertTrue(cache.has(key));
        assertEquals(page, cache.get(key));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        // Ключ не должен быть нулём
        cache.put(null, null);
    }
}