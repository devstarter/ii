package org.ayfaar.app.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class TermAddEvent extends ApplicationEvent{

    @Getter
    protected String term;

    public TermAddEvent(String term) {
        super("ii event");
        this.term = term;
    }
}
