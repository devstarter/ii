package org.ayfaar.app.utils;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.dao.TermMorphDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.hibernate.criterion.MatchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TermsTaggingUpdater {
    @Autowired
    private TermsMarker termsMarker;
    @Autowired
    private TermMorphDao termMorphDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private LinkDao linkDao;
    @Autowired
    private TermDao termDao;
//    @Autowired ApplicationEventPublisher publisher;

    public void update(String termName) {
        long time = System.currentTimeMillis();
        final List<String> aliases = termMorphDao.getAllMorphs(termName);
        updateTerms(termDao.getLike("description", aliases, MatchMode.ANYWHERE));
        updateTerms(termDao.getLike("shortDescription", aliases, MatchMode.ANYWHERE));
        final List<Item> items = itemDao.getLike("content", aliases, MatchMode.ANYWHERE);
        update(items);
        updateLinks(linkDao.getLike("quote", aliases, MatchMode.ANYWHERE));

        final String duration = DurationFormatUtils.formatDuration(System.currentTimeMillis() - time, "HH:mm:ss");
//        publisher.publishEvent(new SimplePushEvent(format("Тегирование для %s завершено за %s", termName, duration)));
    }

    public void updateAllContent() {
        update(itemDao.getAll());
    }


    public void updateAllTerms() {
        updateTerms(termDao.getAll());
    }

    public void updateAllQuotes() {
        updateLinks(linkDao.getAll());
    }

    public void update(List<Item> items) {
        items.stream()
                .parallel()
                .filter((item) -> item.getUpdatesAt() == null)
                .forEach(this::update);
    }

    private void updateTerms(List<Term> terms) {
        for (Term term : terms) {
            term.setTaggedDescription(termsMarker.mark(term.getDescription()));
            term.setTaggedShortDescription(termsMarker.mark(term.getShortDescription()));
            termDao.save(term);
        }
    }

    private void updateLinks(List<Link> links) {
        for (Link link : links) {
//            if (link.getQuote() != null && link.getTaggedQuote() == null) {
                link.setTaggedQuote(termsMarker.mark(link.getQuote()));
                linkDao.save(link);
//                System.out.println(link.getLinkId());
//            }
        }
    }

    public void updateSingle(String morphAlias) {
        long time = System.currentTimeMillis();
        update(itemDao.getLike("content", morphAlias, MatchMode.ANYWHERE));
        updateLinks(linkDao.getLike("quote", morphAlias, MatchMode.ANYWHERE));

        final String duration = DurationFormatUtils.formatDuration(System.currentTimeMillis() - time, "HH:mm:ss");
//        publisher.publishEvent(new SimplePushEvent(format("Тегирование для %s завершено за %s", morphAlias, duration)));
    }

    public void update(Item item) {
        item.setTaggedContent(termsMarker.mark(item.getContent()));
        item.setUpdatesAt(new Date());
        itemDao.save(item);
    }
}
