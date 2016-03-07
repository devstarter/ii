package org.ayfaar.app.contents;

/**
 * Created by Nayil on 10.03.2016.
 */
public class IncorrectSheetException extends XLSParsingException {
    public IncorrectSheetException(String message){
        super(message);
    }
    public IncorrectSheetException(String message, Throwable cause){
        super(message,cause);
    }
}
