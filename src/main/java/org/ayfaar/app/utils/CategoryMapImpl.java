package org.ayfaar.app.utils;

import lombok.Getter;
import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class CategoryMapImpl implements CategoryMap {
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;

    private Map<String, CategoryProvider> categoryMap;

    @PostConstruct
    public void load() {
        categoryMap = new HashMap<String, CategoryProvider>();
        List<Category> categories = categoryDao.getAll();

        for(Category category : categories) {
            categoryMap.put(category.getName(),
                    new CategoryProviderImpl(category.getUri(), category.getParent(), category.getStart(), category.getNext()));
            if(category.isParagraph()) {
                addProvidersForItems(category);
            }
        }
    }

    private void addProvidersForItems(Category category) {
        Item currentItem = itemDao.get(category.getStart());
        String itemNumber = currentItem.getNumber();

        String endNumber = category.getEnd() != null ? getValueFromUri(Item.class, category.getEnd()): itemNumber;

        categoryMap.put(itemNumber, new CategoryProviderImpl(
                currentItem.getUri(), category.getUri(), currentItem.getUri(), ItemController.getNext(itemNumber)));
        while(!itemNumber.equals(endNumber)) {
            itemNumber = ItemController.getNext(itemNumber);
            categoryMap.put(itemNumber, new CategoryProviderImpl(
                    UriGenerator.generate(Item.class, itemNumber), category.getUri(), currentItem.getUri(),
                                          ItemController.getNext(itemNumber)));
        }
    }

    @Override
    public CategoryProvider getProviderForCategory(String name) {
        return categoryMap.get(name);
    }


    /**
     * Возможно категории не нужны вообще, для построения содержания должно хватить провайдеров
     */
    @Override
    public Category getCategory(String name) {
      /*  CategoryProvider provider = categoryMap.get(name);
        //boolean isItem = false
        if(!provider.getUri().startsWith("ии:пункт:")) {
            return provider.getCategory();
        }
        */
        return null;
    }

    /**
     * Не уверен нужен ли тут этот метод или выполнять это в ContentsHelper
     */
    @Override
    public List<CategoryProvider> getParents(String name) {
        List<CategoryProvider> parents = new ArrayList<CategoryProvider>();
        CategoryProvider provider = categoryMap.get(name);

        CategoryProvider parent = provider.getParent();
        if(parent != null) {
            parents.add(parent);
            parents.addAll(getParents(getValueFromUri(Category.class, parent.getUri())));
        }
        return parents;
    }

    public class CategoryProviderImpl implements CategoryProvider {
        @Getter
        private String uri;
        @Getter
        private String parentUri;
        @Getter
        private String start;
        @Getter
        private String next;

        public CategoryProviderImpl(String uri, String parentUri, String start, String next) {
            this.uri = uri;
            this.parentUri = parentUri;
            this.start = start;
            this.next = next;
        }

        @Override
        public Category getCategory() {
            return categoryDao.get("uri", uri);
        }

        @Override
        public List<CategoryProvider> getChildren() {
            List<CategoryProvider> children = new ArrayList<CategoryProvider>();
            if (start != null) {
                Class clazz = getCategory().isParagraph() ? Item.class : Category.class;
                CategoryProvider child = categoryMap.get(getValueFromUri(clazz, start));

                if (child != null) {
                    children.add(child);

                    while (child.getNext() != null) {
                        child = categoryMap.get(getValueFromUri(clazz, child.getNext()));
                        if (!child.getParentUri().equals(uri))
                            break;
                        children.add(child);
                    }
                }
            }
            return children;
        }

        @Override
        public CategoryProvider getParent() {
            return parentUri != null ? categoryMap.get(getValueFromUri(Category.class, parentUri)) : null;
        }
    }
}
