package org.beaverbrooks.domain.user;

import java.util.UUID;

public class Business extends User{

    private String BusinessName;

    public Business(String username) {
        super(username);
        BusinessName = username;
    }

    public Business(String username, UUID id){
        super(username, id);
    }

    @Override
    public String GetName() {
        return BusinessName;
    }
}
