package org.beaverbrooks.shared.exceptions;

public class UserNotFoundException extends Exception {
    public String Message;
    public UserNotFoundException(String message){
        Message = message;
        }
}
