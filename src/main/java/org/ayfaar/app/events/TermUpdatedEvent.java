package org.ayfaar.app.events;

import org.ayfaar.app.model.Term;

public class TermUpdatedEvent extends TermPushEvent {

    public TermUpdatedEvent(Term term, String oldShortDescription, String oldDescription) {
        super(term.getName());
        title = "Обновлён термин: " +term.getName();
        message = "Предыдущий вариант:\n" + oldShortDescription +"\n\n" + oldDescription;
        url = getUrlToTerm(term.getName());
    }

    public TermUpdatedEvent(Term term) {
        this(term, null, null);
    }
}
