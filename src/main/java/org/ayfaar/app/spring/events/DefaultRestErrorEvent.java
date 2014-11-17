package org.ayfaar.app.spring.events;

import org.ayfaar.app.spring.handler.DefaultRestErrorResolver;

/**
 * Created by Pas8sion on 09.11.2014.
 */
public class DefaultRestErrorEvent extends BasicPushEvent {


    public DefaultRestErrorEvent(String title, String message) {
        super();
        this.title = title;
        this.message = message;
    }


}
