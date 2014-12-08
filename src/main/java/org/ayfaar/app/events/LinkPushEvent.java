package org.ayfaar.app.events;

public class LinkPushEvent extends BasicPushEvent implements HasUrl {

    protected String url;

    @Override
    public String getUrl() {
        return url;
    }
}
