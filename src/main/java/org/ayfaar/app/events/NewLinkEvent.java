package org.ayfaar.app.events;

import org.ayfaar.app.model.Link;

import static java.lang.String.format;

/**
 * Created by Pas8sion on 17.11.2014.
 */
public class NewLinkEvent extends LinkPushEvent {
    public NewLinkEvent(String term, String alias, Link link) {
        super();
        title = format("Связь %s + %s", term, alias);
        url = getUrlToTerm(term);
        message =  (link.getType() != null ? "тип: " + link.getType() + "\n" : "")
                +"удалить: " + getRemoveLink(link.getLinkId());
    }
}
