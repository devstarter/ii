package org.ayfaar.app.events;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;

/**
 * Created by Pas8sion on 17.11.2014.
 */
public class SearchQuoteEvent extends LinkPushEvent {
    public SearchQuoteEvent(Term term, Item item, String quote, Integer linkId) {
        super();
        title = "Связь через поиск (" + term.getName() + " + " + item.getNumber() + ")";
        url = getUrlToTerm(term.getName());
        message = quote+ (linkId == null ? "\n\nНе создана по причине возможной дубликации"
                        : "\n\nудалить связь "+getRemoveLink(linkId));
    }
}
