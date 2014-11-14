package org.ayfaar.app.spring.events;

import org.ayfaar.app.spring.handler.DefaultRestErrorResolver;

/**
 * Created by Pas8sion on 09.11.2014.
 */
public class DefaultRestErrorEvent extends BasicPushEvent {

    private Exception ex;
    public DefaultRestErrorEvent(DefaultRestErrorResolver defaultRestErrorResolver, Exception ex) {
        super(defaultRestErrorResolver);
        this.ex = ex;

        System.out.println(ex);
    }

    public Exception getEx() {
        return ex;
    }
}
