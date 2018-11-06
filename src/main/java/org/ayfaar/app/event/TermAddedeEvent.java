package org.ayfaar.app.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class TermAddedeEvent extends ApplicationEvent{

    @Getter
    protected String term;

    public TermAddedeEvent(String term) {
        super("ii event");
        this.term = term;
    }
}
