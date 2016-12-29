package org.ayfaar.app.events;

import org.ayfaar.app.model.Link;
import org.springframework.util.Assert;

public class NewLinkEvent {
    public final Link link;

    public NewLinkEvent(Link link) {
        Assert.notNull(link, "Link is null");
        this.link = link;
    }
}
