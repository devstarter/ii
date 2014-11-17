package org.ayfaar.app.spring.events;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;

/**
 * Created by Pas8sion on 17.11.2014.
 */
public class RateEvent extends BasicPushEvent {
    public RateEvent(Term term, Item item, String quote, Integer linkId) {
        super();
        title = "INFO: Связь через поиск (" + term.getName() + " + " + item.getNumber() + ")";
        message = quote+ (linkId == null ? "\n\nНе создана по причине возможной дубликации"
                        : "удалить связь "+getRemoveLink(linkId));
    }
}
