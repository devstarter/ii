package org.ayfaar.app.utils.exceptions;

import org.ayfaar.app.services.moderation.Action;

public class ConfirmationRequiredException extends LogicalException {
    public Action action;

    public ConfirmationRequiredException(Action action) {
        super(Exceptions.CONFIRMATION_REQUIRED, action.toString());
        this.action = action;
    }
}
