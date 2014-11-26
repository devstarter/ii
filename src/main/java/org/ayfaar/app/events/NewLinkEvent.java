package org.ayfaar.app.events;

import org.ayfaar.app.model.Link;

import static java.lang.String.format;

/**
 * Created by Pas8sion on 17.11.2014.
 */
public class NewLinkEvent extends BasicPushEvent {
    public NewLinkEvent(String term, String alias, Link link) {
        super();
        title = format("Создана связь %s терминов %s + %s", link.getType() != null ? link.getType() : "", term, alias);
        message = getUrlToTerm(term) + " " + getUrlToTerm(alias)
                + "\n\nудалить: " + getRemoveLink(link.getLinkId());
    }
}
