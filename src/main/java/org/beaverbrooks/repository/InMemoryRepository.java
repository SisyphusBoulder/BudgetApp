package org.beaverbrooks.repository;

import org.beaverbrooks.domain.user.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryRepository implements IDataRepository {


    public ArrayList<IUser> dummyUserData;
    public ArrayList<IUserAuth> dummyAuthData;
    public InMemoryRepository(ArrayList<IUser> userData, ArrayList<IUserAuth> userAuthData) {
        dummyUserData = userData;
        dummyAuthData = userAuthData;
    }


    @Override
    public Optional<IUser> FindByName(String name) {
        return dummyUserData.stream()
                .filter(x -> x.GetUsername().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void SaveNewUser(IUserAuth auth, IUser user) {
        dummyAuthData.add(auth);
        dummyUserData.add(user);

    }

    @Override
    public void SaveUpdateUser(IUser user) {
        dummyUserData.add(user);


    }

    @Override
    public Optional<IUser> GetUserById(UUID id) {
        return dummyUserData.stream()
                .filter(x -> Objects.equals(x.GetID(), id))
                .findFirst();
    }

    @Override
    public Optional<IUserAuth> GetUserAuthById(UUID id) {

        return dummyAuthData.stream()
                .filter(x -> x.GetID().orElseThrow().equals(id))
                .findFirst();
    }

    @Override
    public BigDecimal GetAccountBalance(UUID id) {
        return dummyUserData.stream()
                .filter(x -> x.GetID().equals(id))
                .findFirst()
                .orElseThrow()
                .GetAccount()
                .orElseThrow()
                .GetAccountBalance();
    }

    @Override
    public void DeleteAccount(IUser user) {
        dummyUserData.remove(user);
        dummyAuthData.remove(GetUserAuthById(user.GetID()).orElseThrow());
    }

    @Override
    public ArrayList<IUser> GetAllUsers() {
        return dummyUserData;
    }

    @Override
    public ArrayList<IUserAuth> GetAllUserAuth() {
        return dummyAuthData;
    }


}
