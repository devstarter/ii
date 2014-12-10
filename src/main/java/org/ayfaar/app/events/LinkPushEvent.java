package org.ayfaar.app.events;

public class LinkPushEvent extends PushEvent implements HasUrl {

    protected String url;

    @Override
    public String getUrl() {
        return url;
    }
}
