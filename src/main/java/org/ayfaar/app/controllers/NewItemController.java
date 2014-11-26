package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.CategoryMap;
import org.ayfaar.app.utils.TermsMarker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.controllers.ItemController.getPrev;
import static org.ayfaar.app.utils.UriGenerator.generate;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;
import static org.springframework.util.Assert.notNull;
import static org.ayfaar.app.utils.CategoryMap.CategoryProvider;

@Controller
@RequestMapping("api/v2/item")
public class NewItemController {

    @Inject ItemDao itemDao;
    @Inject TermsMarker termsMarker;
    @Inject CategoryMap categoryMap;

    @RequestMapping
    @ResponseBody
    public ItemPresentation get(@RequestParam String number) {
        Item item = itemDao.getByNumber(number);
        notNull(item, "Item not found");

        String markedContent = termsMarker.mark(item.getContent());

        return new ItemPresentation(item, markedContent, generate(Item.class, getPrev(item.getNumber())));
}

    private class ItemPresentation {
        public final String number;
        public final String content;
        public final String next;
        public final String previous;
        public final List<String> parents;

        public ItemPresentation(Item item, String content, String previous) {
            this.content = content;
            this.previous = previous;
            number = item.getNumber();
            next = item.getNext();
            parents = getParents(item.getNumber());
        }
    }


    @RequestMapping("{number}/content")
    @ResponseBody
    public String getContent(@PathVariable String number) {
        Item item = itemDao.getByNumber(number);
        if (item != null) {
            return termsMarker.mark(item.getContent());
        }
        return null;
    }

    private List<String> getParents(String number) {
        List<String> parents = new ArrayList<String>();
        CategoryProvider provider = categoryMap.getProviderByItemNumber(number);

        parents.add(extractCategoryName(provider.getUri()));
        for(CategoryProvider parent : provider.getParents(getValueFromUri(Category.class, provider.getUri()))) {
            parents.add(extractCategoryName(parent.getUri()));
        }
        return parents;
    }

    private String extractCategoryName(String uri) {
        String[] names = uri.split("/|:");
        return names[names.length-1].trim();
    }
}
