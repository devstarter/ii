package org.ayfaar.app.event;

import org.springframework.context.ApplicationEvent;

public abstract class PushEvent extends ApplicationEvent {
    public static final String BASE_URL = "http://ii.ayfaar.ru";

    protected String title;
    protected String message;

    public PushEvent() {
        super("ii event");
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    protected String getRemoveLink(Integer linkId) {
        return BASE_URL+"/api/link/remove/" + linkId;
    }
}
