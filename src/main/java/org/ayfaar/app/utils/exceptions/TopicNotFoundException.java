package org.ayfaar.app.utils.exceptions;

public class TopicNotFoundException extends RuntimeException{

    private String code;
    private String message;


    public TopicNotFoundException(String message, String errorCode){
        super(message);
        this.code = errorCode;
        this.message = message;
    }

    public String getErrorCode(){
        return this.code;
    }

    public void setErrorCode(String errorCode) {
        this.code = code;
    }
}
