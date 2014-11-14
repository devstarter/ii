package org.ayfaar.app.spring.events;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Pas8sion on 09.11.2014.
 */
public abstract class BasicPushEvent extends ApplicationEvent {
    public BasicPushEvent(Object source) {
        super(source);
    }
}
