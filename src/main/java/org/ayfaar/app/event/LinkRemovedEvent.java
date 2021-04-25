package org.ayfaar.app.event;

import org.ayfaar.app.model.Link;
import org.ayfaar.app.services.links.LinkProvider;
import org.springframework.util.Assert;

public class LinkRemovedEvent {
    public final LinkProvider link;

    public LinkRemovedEvent(LinkProvider link) {
        Assert.notNull(link, "Link is null");
        this.link = link;
    }
}
