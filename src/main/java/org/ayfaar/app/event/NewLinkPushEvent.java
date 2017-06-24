package org.ayfaar.app.event;

import org.ayfaar.app.model.Link;

import static java.lang.String.format;

public class NewLinkPushEvent extends LinkPushEvent {
    public NewLinkPushEvent(String term, String alias, Link link) {
        super(format("Связь %s + %s", term, alias), term);
        message =  (link.getType() != null ? "тип: " + link.getType() + "\n" : "")
                +"удалить: " + getRemoveLink(link.getLinkId());
    }
}
