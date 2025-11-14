package org.beaverbrooks.service.auth;

import org.beaverbrooks.domain.user.IUser;
import org.beaverbrooks.domain.user.IUserAuth;
import org.beaverbrooks.repository.IDataRepository;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.PasswordMismatchException;
import org.beaverbrooks.shared.exceptions.UserNotFoundException;

import java.util.*;

public class BasicAuthService implements IAuthService {


    private final Map<UUID, AuthToken> sessions = new HashMap<>();
    private final IDataRepository dataRepository;

    public BasicAuthService(IDataRepository repository) {
        dataRepository = repository;
    }

    @Override
    public void CreateUserSession(IUserAuth userAuth) throws NoSuchElementException {
        sessions.put(userAuth.GetID().orElseThrow(), new AuthToken(userAuth.GetID().orElseThrow(), true));
    }

    @Override
    public IUser UserLogin(String username, String password) throws UserNotFoundException, PasswordMismatchException, NoSuchElementException {
        Optional<IUser> optionalUser = dataRepository.FindByName(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist in the database.");
        }
        IUser user = optionalUser.get();
        Optional<IUserAuth> auth = dataRepository.GetUserAuthById(user.GetID());
        if (auth.isEmpty()){
            throw new UserNotFoundException("User exists but has no auth information!");
        }

        Boolean passwordMatch = ComparePasswords(auth.get(), password);
        if (!passwordMatch) {
            throw new PasswordMismatchException("Username or password is incorrect.");
        }
        sessions.put(user.GetID(),new AuthToken(user.GetID(), true));
        return user;
    }

    @Override
    public void UserLogout() {

    }

//    @Override
//    public Optional<IUser> GetUserFromToken(AuthToken token) {
//        return Optional.ofNullable(sessions.get(token));
//    }



    @Override
    public Optional<IUserAuth> GetUserAuthFromId(UUID id) {
        return dataRepository.GetUserAuthById(id);
    }

    @Override
    public boolean AuthAction(UUID userID, AuthToken token) {
        if (!token.IsAuthed()){
            return false;
        }
        return token.UserId().equals(userID);
    }

    @Override
    public Optional<AuthToken> GetSessionToken(UUID userID) {
        return Optional.ofNullable(sessions.get(userID));
    }

    @Override
    public String GetPassword(String username) {
        return "";
    }

    private Boolean ComparePasswords(IUserAuth auth, String inputPassword) {
        return auth.ComparePassword(inputPassword);
    }
}
