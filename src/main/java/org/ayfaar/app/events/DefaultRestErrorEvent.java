package org.ayfaar.app.events;

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
