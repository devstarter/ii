package org.ayfaar.app.event;

import org.ayfaar.app.model.Term;

public class TermUpdatedEvent extends TermPushEvent {

    public String morphAlias;

    public TermUpdatedEvent(Term term, String oldShortDescription, String oldDescription) {
        super(term.getName());
        title = "Обновлён термин: " +term.getName();
        message = "Предыдущий вариант:\n" + oldShortDescription +"\n\n" + oldDescription;
        url = getUrlToTerm(term.getName());
    }

    public TermUpdatedEvent(Term term) {
        this(term, null, null);
    }

    public TermUpdatedEvent(Term term, String morphAlias) {
        this(term);
        this.morphAlias = morphAlias;
    }
}
