package org.ayfaar.app.event;

import org.ayfaar.app.model.Link;
import org.ayfaar.app.services.links.LinkProvider;
import org.springframework.util.Assert;

import java.util.List;

public class LinksRemovedEvent {
    public final List<LinkProvider> links;

    public LinksRemovedEvent(List<LinkProvider> links) {
        Assert.notNull(links, "Link is null");
        this.links = links;
    }
}
