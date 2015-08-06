package org.ayfaar.app.utils;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CategoryMapImplIntegrationTest extends IntegrationTest{
    @Autowired
    private CategoryMapImpl categoryMap;

    @Test
    public void testGetChildrenForSection() {
        CategoryMap.CategoryProvider provider = categoryMap.getProviderForCategory("БДК/Раздел I");
        List<CategoryMap.CategoryProvider> children = provider.getChildren();

        assertEquals(4, children.size());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), children.get(0).getUri());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 4"), children.get(3).getUri());
    }

    @Test
    public void testGetParentForParagraph() {
        CategoryMap.CategoryProvider provider = categoryMap.getProviderForCategory("параграф:10.1.1.1");
        CategoryMap.CategoryProvider parent = provider.getParent();

        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), parent.getUri());
    }

    @Test
    public void testGetParentsFotParagraph() {
        CategoryMap.CategoryProvider provider = categoryMap.getProviderForCategory("параграф:10.1.1.1");
        List<CategoryMap.CategoryProvider> parents = provider.getParents();

        assertEquals(5, parents.size());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I/Глава 1"), parents.get(0).getUri());
        assertEquals(UriGenerator.generate(Category.class, "БДК/Раздел I"), parents.get(1).getUri());
        assertEquals(UriGenerator.generate(Category.class, "Том 10"), parents.get(2).getUri());
        assertEquals(UriGenerator.generate(Category.class, "БДК"), parents.get(3).getUri());
        assertEquals(UriGenerator.generate(Category.class, "Содержание"), parents.get(4).getUri());
    }

    @Test
    public void testGetProviderByItemNumber() {
        CategoryMap.CategoryProvider provider = categoryMap.getProviderByItemNumber("10.10037");
        assertEquals(UriGenerator.generate(Category.class, "параграф:10.1.1.4"), provider.getUri());
        provider = categoryMap.getProviderByItemNumber("14.15639");
        assertEquals(UriGenerator.generate(Category.class, "параграф:14.16.2.14"), provider.getUri());
        provider = categoryMap.getProviderByItemNumber("14.15598");
        assertEquals(UriGenerator.generate(Category.class, "параграф:14.16.2.4"), provider.getUri());
    }
}
