package org.ayfaar.app.utils;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermMorphDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.hibernate.criterion.MatchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityUpdater {
    @Autowired
    private TermsMarker termsMarker;
    @Autowired
    private TermMorphDao termMorphDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private LinkDao linkDao;

    public void updateItemContent(String name) {
        updateItems(itemDao.getLike("content", termMorphDao.getAllMorphs(name), MatchMode.ANYWHERE));
    }

    public void updateLinkQuotes(String name) {
        updateLinks(linkDao.getLike("quote", termMorphDao.getAllMorphs(name), MatchMode.ANYWHERE));
    }

    public void updateAllContent() {
        updateItems(itemDao.getAll());
    }

    public void updateAllQuotes() {
        updateLinks(linkDao.getAll());
    }

    private void updateItems(List<Item> items) {
        for (Item item : items) {
            item.setTaggedContent(termsMarker.mark(item.getContent()));
            itemDao.save(item);
        }
    }

    private void updateLinks(List<Link> links) {
        for (Link link : links) {
            link.setTaggedQuote(termsMarker.mark(link.getQuote()));
            linkDao.save(link);
        }
    }
}
