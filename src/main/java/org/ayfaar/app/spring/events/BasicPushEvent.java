package org.ayfaar.app.spring.events;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Pas8sion on 09.11.2014.
 */
public abstract class BasicPushEvent extends ApplicationEvent {

    protected String title;
    protected String message;

    public BasicPushEvent() {
        super("ii event");
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    protected String getRemoveLink(Integer linkId) {
        return "http://ii.ayfaar.org/api/link/remove/" + linkId;
    }

}
