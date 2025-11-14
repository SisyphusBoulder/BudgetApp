package org.beaverbrooks.domain.user;
import java.util.UUID;

public class Customer extends User{

    private final String FirstName;
    private final String LastName;

    public Customer(String username, String firstName, String lastName){
        super(username);
        FirstName = firstName;
        LastName = lastName;
    }

    public Customer(String username, String firstName, String lastName, UUID id){
        super(username, id);
        FirstName = firstName;
        LastName = lastName;
    }


    @Override
    public String GetName() {
        return FirstName + " " + LastName;
    }
}
