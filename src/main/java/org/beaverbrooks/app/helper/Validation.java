package org.beaverbrooks.app.helper;

import org.beaverbrooks.shared.exceptions.ValidationException;

import java.util.regex.Pattern;

public class Validation {

    public final Pattern usernamePattern = Pattern.compile("[^a-zA-Z\\-]", Pattern.CASE_INSENSITIVE);

    public final Pattern namePattern = Pattern.compile("[^a-zA-Z\\s]", Pattern.CASE_INSENSITIVE);


    public boolean ValidateUsername(String username) throws ValidationException {
        String trimmedName = username.trim();
        if (trimmedName.isBlank()){
            throw new ValidationException("Username was empty");
        }
        if (trimmedName.length() <= 5 || trimmedName.length() >20){
            throw new ValidationException("Username length incorrect");
        }
        if (usernamePattern.matcher(trimmedName).find()){
            throw new ValidationException("Illegal characters in username");
        }
        return true;

    }

    public boolean ValidatePassword(String password) throws ValidationException {
        String trimmedPassword = password.trim();

        if (trimmedPassword.isBlank()){
            throw  new ValidationException("Password was empty");
        }
        if (trimmedPassword.length() < 12 || trimmedPassword.length() > 64){
            throw new ValidationException("Password length incorrect");
        }

        int numberOfDigits = 0;
        int numberOfSpecials = 0;
        for ( Character c : trimmedPassword.toCharArray()){
            if (Character.isDigit(c)){
                numberOfDigits +=1 ;
            }
            if (!Character.isDigit(c) && !Character.isAlphabetic(c) && !Character.isSpaceChar(c)){
                numberOfSpecials += 1;
            }
        }
        if (numberOfDigits < 2 || numberOfSpecials < 2){
            throw new ValidationException("Invalid password signature");
        }
        return true;
    }

    public boolean ValidateName(String first, String last) throws ValidationException{
        String firstTrim = first.trim();
        String lastTrim = last.trim();

        if (firstTrim.isBlank() || lastTrim.isBlank()){
            throw new ValidationException("Name was empty");
        }

        if(usernamePattern.matcher(firstTrim).find() || usernamePattern.matcher(lastTrim).find()){
            throw new ValidationException("Name contains illegal characters");
        }

        return true;
    }
    public boolean ValidateBusinessName(String businessName) throws ValidationException{
        String businessNameTrim = businessName.trim();

        if (businessNameTrim.isBlank()){
            throw new ValidationException("Business name was empty");
        }

        if(namePattern.matcher(businessNameTrim).find()){
            throw new ValidationException("Name contains illegal characters");
        }

        return true;
    }
}


