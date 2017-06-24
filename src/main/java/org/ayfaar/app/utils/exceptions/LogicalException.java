package org.ayfaar.app.utils.exceptions;

import org.slf4j.helpers.MessageFormatter;

public class LogicalException extends RuntimeException {

    private ExceptionCode code;

    public LogicalException(ExceptionCode code, Object... params){
        super(MessageFormatter.arrayFormat(code.getMessage(), params).getMessage());
        this.code = code;
    }

    public ExceptionCode getCode() {
        return code;
    }

    public void setCode(ExceptionCode code) {
        this.code = code;
    }
}

