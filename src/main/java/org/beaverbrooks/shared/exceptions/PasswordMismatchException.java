package org.beaverbrooks.shared.exceptions;

public class PasswordMismatchException extends Exception {
    public String Message;
    public PasswordMismatchException(String s) {
        Message = s;
    }
}
