package org.ayfaar.app.events;

import org.ayfaar.app.model.Term;

public class TermUpdatedEvent extends BasicPushEvent {
    public TermUpdatedEvent(Term term, String oldShortDescription, String oldDescription) {
        super();
        title = "Обновлён термин: " +term.getName();
        message = "Предыдущий вариант:\n" + oldShortDescription +"\n\n" + oldDescription;
    }
}
