package org.beaverbrooks.domain.user;

import org.beaverbrooks.domain.account.IAccount;

import java.util.Optional;
import java.util.UUID;

public interface IUser {

    UUID GetID();

    String GetUsername();

    Optional<IAccount> GetAccount();

    String GetName();


}
