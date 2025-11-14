package org.beaverbrooks.repository;

import org.beaverbrooks.domain.user.IUser;
import org.beaverbrooks.domain.user.IUserAuth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface IDataRepository {

    Optional<IUser> FindByName(String name);

    void SaveNewUser(IUserAuth auth, IUser user);

    void SaveUpdateUser(IUser user);

    Optional<IUser> GetUserById(UUID id);

    Optional<IUserAuth> GetUserAuthById(UUID id);

    BigDecimal GetAccountBalance(UUID id);

    void DeleteAccount(IUser user);

    ArrayList<IUser> GetAllUsers();

    ArrayList<IUserAuth> GetAllUserAuth();
}
