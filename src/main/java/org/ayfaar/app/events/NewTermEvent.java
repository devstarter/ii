package org.ayfaar.app.events;

import org.ayfaar.app.model.Term;

public class NewTermEvent extends PushEvent {
    public NewTermEvent(Term term) {
        super();
        title = "Новый термин: "+term.getName();
        message = term.getShortDescription() + "\n\n" + term.getDescription();
    }
}
