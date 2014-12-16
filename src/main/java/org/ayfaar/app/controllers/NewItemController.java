package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.CategoryMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.ayfaar.app.controllers.ItemController.getPrev;
import static org.ayfaar.app.utils.CategoryMap.CategoryProvider;
import static org.ayfaar.app.utils.UriGenerator.generate;
import static org.springframework.util.Assert.notNull;

@Controller
@RequestMapping("api/v2/item")
public class NewItemController {

    private static final int MAXIMUM_RANGE_SIZE = 50;
    @Inject ItemDao itemDao;
    @Inject CategoryMap categoryMap;

    @RequestMapping
    @ResponseBody
    public ItemPresentation get(@RequestParam String number) {
        Item item = itemDao.getByNumber(number);
        notNull(item, format("Item `%s` not found", number));

        return new ItemPresentation(item, generate(Item.class, getPrev(item.getNumber())));
    }

    @RequestMapping("range")
    @ResponseBody
    public List<ItemPresentation> get(@RequestParam String from, @RequestParam String to) {
        Item item = itemDao.getByNumber(from);
        notNull(item, format("Item `%s` not found", from));
        List<ItemPresentation> items = new ArrayList<ItemPresentation>();

        final ItemPresentation itemPresentation = new ItemPresentation(item);
        itemPresentation.parents = getParents(item.getNumber());
        items.add(itemPresentation);

        while (!item.getNumber().equals(to)) {
            item = itemDao.get(item.getNext());
            items.add(new ItemPresentation(item, item.getTaggedContent()));
            if (items.size() > MAXIMUM_RANGE_SIZE) {
                throw new RuntimeException(format("Maximum range size reached (from %s to %s)", from, to));
            }
        }

        return items;
    }

    private class ItemPresentation {
        public final String number;
        public final String content;
        public String uri;
        public String next;
        public String previous;
        public List<ParentPresentation> parents;

        public ItemPresentation(Item item, String previous) {
            this.content = item.getTaggedContent();
            this.previous = previous;
            number = item.getNumber();
            next = item.getNext();
            parents = getParents(item.getNumber());
        }
        public ItemPresentation(Item item) {
            this(item, null);
        }
    }

    private class ParentPresentation {
        public final String name;
        public final String uri;

        public ParentPresentation(String name, String uri) {
            this.name = name;
            this.uri = uri;
        }
    }

    @RequestMapping("{number}/content")
    @ResponseBody
    public String getContent(@PathVariable String number) {
        Item item = itemDao.getByNumber(number);
        if (item != null) {
            return item.getTaggedContent();
        }
        return null;
    }

     private List<ParentPresentation> getParents(String number) {
         List<ParentPresentation> parents = new ArrayList<ParentPresentation>();
         CategoryProvider provider = categoryMap.getProviderByItemNumber(number);
         if (provider != null) {
             parents.add(new ParentPresentation(provider.extractCategoryName(), provider.getUri()));
             for (CategoryProvider parent : provider.getParents()) {
                 parents.add(new ParentPresentation(parent.extractCategoryName(), parent.getUri()));
             }
         }
         return parents;
    }
}
