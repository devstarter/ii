package org.ayfaar.app.services;

import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Map;
import java.util.function.Function;

@Service
public class ItemService {
    private final ItemDao itemDao;
    private Map<String, String> allUriNumbers;

    @Inject
    public ItemService(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @PostConstruct
    private void init() {
        allUriNumbers = StreamEx.of(itemDao.getAllNumbers())
                .sorted()
                .toSortedMap(n -> UriGenerator.generate(Item.class, n), Function.identity());
    }

    public Map<String, String> getAllUriNumbers() {
        return allUriNumbers;
    }

    public Item get(String uri) {
        Item item = itemDao.get(uri);
        if (item == null) throw new RuntimeException("Item for uri "+uri+" not found");
        return item;
    }

    public void save(Item item) {
        itemDao.save(item);
    }
}
