package org.beaverbrooks.domain.user;

import org.beaverbrooks.domain.account.CustomerAccount;
import org.beaverbrooks.domain.account.IAccount;

import java.util.Optional;
import java.util.UUID;

public abstract class User implements  IUser{

    private final UUID UserID;
    private final String Username;
    private final IAccount Account;

    public User(String username){
        UserID = UUID.randomUUID();
        Username = username;
        Account = new CustomerAccount();
    }

    //For testing purposes/creating dummy data
    public User(String username, UUID id){
        UserID = id;
        Username = username;
        Account = new CustomerAccount();
    }

    @Override
    public UUID GetID() {
        return UserID;
    }

    @Override
    public String GetUsername() {
        return Username;
    }

    @Override
    public Optional<IAccount> GetAccount() {
        return Optional.ofNullable(Account);
    }

    @Override
    public abstract String GetName();


}

