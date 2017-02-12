package org.ayfaar.app.utils;

import org.ayfaar.app.event.SysLogEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class SysLogPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void log(String message) {
        SysLogEvent event = new SysLogEvent(this, message);
        publisher.publishEvent(event);
    }
}
