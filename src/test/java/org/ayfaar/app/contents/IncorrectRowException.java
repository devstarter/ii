package org.ayfaar.app.contents;

/**
 * Created by Nayil on 10.03.2016.
 */
public class IncorrectRowException  extends XLSParsingException{
    public IncorrectRowException(String message){
        super(message);
    }
    public IncorrectRowException(String message, Throwable cause){
        super(message,cause);
    }
}
