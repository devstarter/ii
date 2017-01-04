package org.ayfaar.app.event;

/**
 * Created by Pas8sion on 09.11.2014.
 */
public class DefaultRestErrorEvent extends PushEvent {


    public DefaultRestErrorEvent(String title, String message) {
        super();
        this.title = title;
        this.message = message;
    }


}
