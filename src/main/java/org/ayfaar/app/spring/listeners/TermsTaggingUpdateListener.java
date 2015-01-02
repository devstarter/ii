package org.ayfaar.app.spring.listeners;

import org.ayfaar.app.events.PushEvent;
import org.ayfaar.app.events.TermPushEvent;
import org.ayfaar.app.events.TermUpdatedEvent;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class TermsTaggingUpdateListener implements ApplicationListener<PushEvent> {
    @Autowired
    private TermsTaggingUpdater taggingUpdater;

    @Override
    public void onApplicationEvent(final PushEvent event) {
        if(event instanceof TermPushEvent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (event instanceof TermUpdatedEvent && ((TermUpdatedEvent)event).morphAlias != null) {
                        taggingUpdater.updateSingle(((TermUpdatedEvent)event).morphAlias);
                    } else {
                        taggingUpdater.update(((TermPushEvent) event).getName());
                    }
                }
            }).start();
        }
    }
}
