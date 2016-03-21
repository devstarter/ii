package org.ayfaar.app.utils.exceptions;

public class LogicalException extends RuntimeException{

    private Exceptions code;

    public LogicalException(Exceptions code, String... params){
        super(String.format(code.getMessage(), params));
        this.code = code;
    }

    public Exceptions getExceptions() {
        return code;
    }

    public void setCode(Exceptions code) {
        this.code = code;
    }
}

