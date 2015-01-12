package org.ayfaar.app.events;

import org.ayfaar.app.model.Term;

public class NewTermEvent extends TermPushEvent {

    public NewTermEvent(Term term) {
        super(term.getName());
        title = "Новый термин: "+term.getName();
        message = term.getShortDescription() + "\n\n" + term.getDescription();
    }
}
