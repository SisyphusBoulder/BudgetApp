package org.beaverbrooks.service.auth;

import org.beaverbrooks.domain.user.IUser;
import org.beaverbrooks.domain.user.IUserAuth;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.PasswordMismatchException;
import org.beaverbrooks.shared.exceptions.UserNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public interface IAuthService {

    IUser UserLogin(String username, String password) throws UserNotFoundException, PasswordMismatchException, NoSuchElementException;

    void CreateUserSession(IUserAuth userAuth) throws NoSuchElementException;

    void UserLogout();

    String GetPassword(String username);

    Optional<IUserAuth> GetUserAuthFromId(UUID id);

    boolean AuthAction(UUID userID, AuthToken token);

    Optional<AuthToken> GetSessionToken(UUID id);



}
