package org.beaverbrooks.shared.exceptions;

public class ValidationException extends Exception{

    public final String Message;

    public ValidationException(String message){
        Message = message;
    }

}
