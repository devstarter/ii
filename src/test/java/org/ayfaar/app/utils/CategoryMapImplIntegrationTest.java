package org.ayfaar.app.utils;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CategoryMapImplIntegrationTest extends IntegrationTest{
    @Autowired
    private CategoryMapImpl categoryMap;

    @Test
    public void testGetChildrenForSection() {
        CategoryMap.CategoryProvider provider = categoryMap.getProviderForCategory("БДК / Раздел I");
        List<CategoryMap.CategoryProvider> children = provider.getChildren();

        assertEquals(4, children.size());
        assertEquals(UriGenerator.generate(Category.class, "БДК / Раздел I / Глава 1"), children.get(0).getUri());
        assertEquals(UriGenerator.generate(Category.class, "БДК / Раздел I / Глава 4"), children.get(3).getUri());
    }

    @Test
    public void testGetChildrenForParagraph() {
        CategoryMap.CategoryProvider provider = categoryMap.getProviderForCategory("Параграф 10.1.1.1");
        List<CategoryMap.CategoryProvider> children = provider.getChildren();

        assertEquals(6, children.size());
        assertEquals(UriGenerator.generate(Item.class, "10.10001"), children.get(0).getUri());
        assertEquals(UriGenerator.generate(Item.class, "10.10006"), children.get(5).getUri());
    }

    @Test
    public void testGetParent() {
        CategoryMap.CategoryProvider provider = categoryMap.getProviderForCategory("Параграф 10.1.1.1");
        CategoryMap.CategoryProvider parent = provider.getParent();

        assertEquals(UriGenerator.generate(Category.class, "БДК / Раздел I / Глава 1"), parent.getUri());
    }

    @Test
    public void testGetParents() {
        List<CategoryMap.CategoryProvider> parents = categoryMap.getParents("Параграф 10.1.1.1");

        assertEquals(4, parents.size());
        assertEquals(UriGenerator.generate(Category.class, "БДК / Раздел I / Глава 1"), parents.get(0).getUri());
        assertEquals(UriGenerator.generate(Category.class, "БДК / Раздел I"), parents.get(1).getUri());
        assertEquals(UriGenerator.generate(Category.class, "Том 10"), parents.get(2).getUri());
        assertEquals(UriGenerator.generate(Category.class, "БДК"), parents.get(3).getUri());
    }
}
