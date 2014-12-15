package org.ayfaar.app.utils;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermMorphDao;
import org.ayfaar.app.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinksUpdater {
    @Autowired
    private LinkDao linkDao;
    @Autowired
    private TermMorphDao termMorphDao;
    @Autowired
    private TermsMarker termsMarker;

    public void updateQuotes(String name) {
        List<String> terms = termMorphDao.getAllMorphs(name);
        update(linkDao.findInQuotes(terms));
    }

    public void updateAllQuotes() {
        update(linkDao.getAll());
    }

    private void update(List<Link> links) {
        for (Link link : links) {
            link.setTaggedQuote(termsMarker.mark(link.getQuote()));
            linkDao.save(link);
        }
    }
}
