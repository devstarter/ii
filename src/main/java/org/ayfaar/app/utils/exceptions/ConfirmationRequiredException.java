package org.ayfaar.app.utils.exceptions;

import org.ayfaar.app.model.PendingAction;

public class ConfirmationRequiredException extends LogicalException {
    public PendingAction action;

    public ConfirmationRequiredException(PendingAction action) {
        super(ExceptionCode.CONFIRMATION_REQUIRED);
        this.action = action;
    }
}
