package org.ayfaar.app.spring.listeners;

import org.ayfaar.app.events.PushEvent;
import org.ayfaar.app.events.TermPushEvent;
import org.ayfaar.app.utils.ItemsUpdater;
import org.ayfaar.app.utils.LinksUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class TermListener implements ApplicationListener<PushEvent> {
    @Autowired
    private ItemsUpdater itemsUpdater;
    @Autowired
    private LinksUpdater linksUpdater;

    @Override
    public void onApplicationEvent(final PushEvent event) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(event instanceof TermPushEvent) {
                    itemsUpdater.updateContent(((TermPushEvent)event).getName());
                    linksUpdater.updateQuotes(((TermPushEvent)event).getName());
                }
            }
        }).start();
    }
}
