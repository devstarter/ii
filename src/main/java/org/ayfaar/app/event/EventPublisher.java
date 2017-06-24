package org.ayfaar.app.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
/**
 * Decorator for event driven methodology
 */
public class EventPublisher {
    final private ApplicationEventPublisher publisher;

    @Inject
    private EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishEvent(Object event) {
        publisher.publishEvent(event);
    }
}
