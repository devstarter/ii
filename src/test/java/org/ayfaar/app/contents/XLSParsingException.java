package org.ayfaar.app.contents;

/**
 * Created by Nayil on 10.03.2016.
 */
public class XLSParsingException extends Exception {
    public XLSParsingException(String message){
        super(message);
    }
    public XLSParsingException(String message, Throwable cause){
        super(message,cause);
    }
}
