package org.ayfaar.app.utils.exceptions;

public class LogicalException extends RuntimeException{

    private String code;
    private String message;


    public LogicalException(Exceptions exceptions){
        super(exceptions.getMessage());
        this.code = exceptions.getMessage();
        this.message = exceptions.getMessage();
    }

    public String getErrorCode(){
        return this.code;
    }

    public void setErrorCode(String errorCode) {
        this.code = code;
    }
}

