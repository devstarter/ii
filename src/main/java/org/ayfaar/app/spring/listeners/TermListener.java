package org.ayfaar.app.spring.listeners;

import org.ayfaar.app.events.PushEvent;
import org.ayfaar.app.events.TermPushEvent;
import org.ayfaar.app.utils.EntityUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class TermListener implements ApplicationListener<PushEvent> {
    @Autowired
    private EntityUpdater entityUpdater;

    @Override
    public void onApplicationEvent(final PushEvent event) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(event instanceof TermPushEvent) {
                    entityUpdater.updateItemContent(((TermPushEvent)event).getName());
                    entityUpdater.updateLinkQuotes(((TermPushEvent)event).getName());
                }
            }
        }).start();
    }
}
