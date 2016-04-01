package org.ayfaar.app.utils.exceptions;

import org.slf4j.helpers.MessageFormatter;

public class LogicalException extends RuntimeException{

    private Exceptions code;

    public LogicalException(Exceptions code, Object... params){
        super(MessageFormatter.arrayFormat(code.getMessage(), params).getMessage());
        this.code = code;
    }

    public Exceptions getExceptions() {
        return code;
    }

    public void setCode(Exceptions code) {
        this.code = code;
    }
}

