package org.ayfaar.app.event;

import org.ayfaar.app.model.TermMorph;
import org.springframework.context.ApplicationEvent;

public class TermMorphAddedEvent extends ApplicationEvent {
    public TermMorphAddedEvent(TermMorph termMorph) {
        super(termMorph);
    }
}