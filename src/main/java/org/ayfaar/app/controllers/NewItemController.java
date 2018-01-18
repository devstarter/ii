package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.services.ItemService;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.ContentsService.ParagraphProvider;
import org.ayfaar.app.utils.TermsMarker;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.ayfaar.app.controllers.ItemController.getPrev;
import static org.ayfaar.app.utils.UriGenerator.generate;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.StringUtils.isEmpty;

@Controller
@RequestMapping("api/v2/item")
public class NewItemController {

    private static final int MAXIMUM_RANGE_SIZE = 200;
    @Inject ItemDao itemDao;
    @Inject ContentsService contentsService;
    @Inject TermsMarker termsMarker;
    @Inject AsyncTaskExecutor taskExecutor;
    @Inject TermsTaggingUpdater taggingUpdater;
    @Inject ItemService itemService;

    @RequestMapping
    @ResponseBody
    public ItemPresentation get(@RequestParam String number) {
        final Item item = itemDao.getByNumber(number);
        notNull(item, format("Item `%s` not found", number));

        if (item.getNext() != null) {
            taskExecutor.execute(() -> {
                final Item nextItem = itemDao.get(item.getNext());
                taggingUpdater.update(nextItem);
            });
        }
        if (isEmpty(item.getTaggedContent())) {
            taggingUpdater.update(item);
            itemDao.save(item);
        }
        return new ItemPresentation(item, generate(Item.class, getPrev(item.getNumber())), true);
    }

    @RequestMapping("{number}/{more}more")
    @ResponseBody
    public List<ItemPresentation> getMore(@PathVariable String number, @PathVariable Integer more) {
        List<ItemPresentation> presentations = new ArrayList<>();

        for (Item item : itemDao.getNext(number, more)) {
            presentations.add(new ItemPresentation(item));
        }
        return presentations;
    }

    @RequestMapping("range")
    @ResponseBody
    public List<ItemPresentation> get(@RequestParam String from, @RequestParam String to) {
        Item item = itemDao.getByNumber(from);
        notNull(item, format("Item `%s` not found", from));
        notNull(itemDao.getByNumber(to), format("Item `%s` not found", to));

        List<ItemPresentation> items = new ArrayList<>();

        final ItemPresentation itemPresentation = new ItemPresentation(item);
//        itemPresentation.parents = parents(item.getNumber());
        items.add(itemPresentation);

        while (!item.getNumber().equals(to)) {
            item = itemService.get(item.getNext());
            if (item.getTaggedContent() == null) {
                item.setTaggedContent(termsMarker.mark(item.getContent()));
                itemService.save(item);
            }
            items.add(new ItemPresentation(item));
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

        public ItemPresentation(Item item, String previous, Boolean loadParents) {
            this.uri = item.getUri();
            this.content = item.getTaggedContent();
            this.previous = previous;
            number = item.getNumber();
            next = item.getNext();
            parents = loadParents ? getParents(item.getNumber()) : null;
        }
        public ItemPresentation(Item item) {
            this(item, null, false);
        }
    }

    public class ParentPresentation {
        public String from;
        public String to;
        public final String name;
        public final String uri;

        public ParentPresentation(String name, String uri) {
            this.name = name;
            this.uri = uri;
        }

        public ParentPresentation(String name, String uri, String from, String to) {
            this(name, uri);
            this.from = from;
            this.to = to;
        }
    }

    @RequestMapping(value = "{number}/content", produces = "text/plain; charset=utf-8")
    @ResponseBody
    public String getContent(@PathVariable String number) {
        Item item = itemDao.getByNumber(number);
        if (item != null) {
            return item.getTaggedContent();
        }
        return null;
    }

    @RequestMapping("{number}/mark")
    public void mark(@PathVariable String number) {
        Item item = itemDao.getByNumber(number);
        if (item != null) {
            item.setTaggedContent(termsMarker.mark(item.getContent()));
            itemDao.save(item);
        }
    }

     private List<ParentPresentation> getParents(String number) {
         List<ParentPresentation> parents = new ArrayList<>();
         Optional<? extends ParagraphProvider> paragraphOpt = contentsService.getByItemNumber(number);
         if (paragraphOpt.isPresent()) {
             final ParagraphProvider paragraph = paragraphOpt.get();
             parents.addAll(paragraph.parents().stream()
                     .map(parent -> new ParentPresentation(parent.extractCategoryName(), parent.uri()))
                     .collect(Collectors.toList()));
             parents.add(new ParentPresentation(paragraph.name(), paragraph.uri(), paragraph.from(), paragraph.to()));
         }
         return parents;
    }
}
