package org.ayfaar.app.event;

public class QuietException extends RuntimeException {
    public QuietException() {
    }

    public QuietException(String message) {
        super(message);
    }

    public QuietException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuietException(Throwable cause) {
        super(cause);
    }

    public QuietException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
