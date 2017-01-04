package org.ayfaar.app.event;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;

public class SearchQuoteEvent extends LinkPushEvent {
    public SearchQuoteEvent(Term term, Item item, String quote, Integer linkId) {
        super("Связь через поиск (" + term.getName() + " + " + item.getNumber() + ")", term.getName());
        message = quote+ (linkId == null ? "\n\nНе создана по причине возможной дубликации"
                        : "\n\nудалить связь "+getRemoveLink(linkId));
    }
}
