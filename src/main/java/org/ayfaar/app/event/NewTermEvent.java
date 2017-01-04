package org.ayfaar.app.event;

import org.ayfaar.app.model.Term;

public class NewTermEvent extends TermPushEvent {

    public NewTermEvent(Term term) {
        super(term.getName());
        title = "Новый термин: "+term.getName();
        if (term.getShortDescription() != null || term.getDescription() != null)
            message = term.getShortDescription() + "\n\n" + term.getDescription();
    }
}
