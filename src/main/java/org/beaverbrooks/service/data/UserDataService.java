package org.beaverbrooks.service.data;

import org.beaverbrooks.domain.user.*;
import org.beaverbrooks.repository.IDataRepository;
import org.beaverbrooks.service.auth.IAuthService;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.AuthorisationException;
import org.beaverbrooks.shared.exceptions.DuplicateUserException;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class UserDataService implements IUserDataService {
    private final IDataRepository DataRepository;
    private final IAuthService AuthService;

    public UserDataService(IDataRepository repository, IAuthService authService) {

        DataRepository = repository;
        AuthService = authService;
    }

    @Override
    public IUser GetUserData(UUID id) {
        return null;
    }

    @Override
    public IUser GetUserData(String username) {
        return null;
    }


    @Override
    public Customer CreateCustomer(String username, String password, String firstName, String lastName) throws NoSuchElementException, DuplicateUserException {
        Customer newCustomer;
        UserAuth newAuth;
        Optional<IUser> existingUser = DataRepository.FindByName(username);
        if (existingUser.isPresent()) {
            throw new DuplicateUserException("User already exists");
        }
        newCustomer = new Customer(username, firstName, lastName);
        newAuth = new UserAuth(newCustomer.GetID(), password);
        DataRepository.SaveNewUser(newAuth, newCustomer);

        IUser newUser = DataRepository.GetUserById(newCustomer.GetID()).orElseThrow();
        AuthService.CreateUserSession(newAuth);
        return (Customer) newUser;
    }

    @Override
    public Business CreateBusiness(String username, String password) throws NoSuchElementException, DuplicateUserException {
        Business newBusiness;
        UserAuth newAuth;
        Optional<IUser> existingBusiness = DataRepository.FindByName(username);
        if (existingBusiness.isPresent()) {
            throw new DuplicateUserException("Business already exists");
        }
        newBusiness = new Business(username);
        newAuth = new UserAuth(newBusiness.GetID(), password);

        DataRepository.SaveNewUser(newAuth, newBusiness);

        AuthService.CreateUserSession(newAuth);
        return (Business) DataRepository.GetUserById(newBusiness.GetID()).orElseThrow();
    }

    @Override
    public BigDecimal GetUserAccountBalance(UUID id, AuthToken token) {
        if (!AuthService.AuthAction(id, token)) {
            throw new AuthorisationException("Not authorised to perform this action");
        }
        return DataRepository.GetAccountBalance(id);
    }

    @Override
    public void AddToUserAccountBalance(IUser user, BigDecimal amount, AuthToken token) {
        if (!AuthService.AuthAction(user.GetID(), token)) {
            throw new AuthorisationException("Not authorised to perform this action");
        }
        user.GetAccount().orElseThrow().AddToAccount(amount);
        DataRepository.SaveUpdateUser(user);
    }

    @Override
    public void WithdrawFromUserAccountBalance(IUser user, BigDecimal amount, AuthToken token) {
        if (!AuthService.AuthAction(user.GetID(), token)) {
            throw new AuthorisationException("Not authorised to perform this action");
        }
        user.GetAccount().orElseThrow().SubtractFromAccount(amount);
        DataRepository.SaveUpdateUser(user);
    }

    @Override
    public void DeleteUserAccount(IUser user, AuthToken token) {
        if (!AuthService.AuthAction(user.GetID(), token)) {
            throw new AuthorisationException("Not authorised to perform this action");
        }
        DataRepository.DeleteAccount(user);
    }


}
