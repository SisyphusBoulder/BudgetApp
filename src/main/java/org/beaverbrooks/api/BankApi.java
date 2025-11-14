package org.beaverbrooks.api;

import org.beaverbrooks.domain.user.Business;
import org.beaverbrooks.domain.user.Customer;
import org.beaverbrooks.domain.user.IUser;
import org.beaverbrooks.service.auth.IAuthService;
import org.beaverbrooks.service.data.IUserDataService;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.DuplicateUserException;
import org.beaverbrooks.shared.exceptions.PasswordMismatchException;
import org.beaverbrooks.shared.exceptions.UnauthorisedException;
import org.beaverbrooks.shared.exceptions.UserNotFoundException;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BankApi implements IBankApi{
    private final IAuthService AuthService;

    private final IUserDataService DataService;

    public BankApi(IAuthService authService, IUserDataService dataService){
        this.AuthService = authService;
        DataService = dataService;
    }

    @Override
    public BigDecimal GetUserBalance(UUID userID) throws UnauthorisedException, NoSuchElementException {
        Optional<AuthToken> token = AuthService.GetSessionToken(userID);

        if(token.isEmpty() || !token.get().IsAuthed()){
            throw new UnauthorisedException("Not authorised to perform this action!");
        }

            return DataService.GetUserAccountBalance(userID, token.get());
    }


    @Override
    public void DepositToAccount(IUser user, BigDecimal amount) throws UnauthorisedException {
        Optional<AuthToken> token = AuthService.GetSessionToken(user.GetID());

        if(token.isEmpty() || !token.get().IsAuthed()){
            throw new UnauthorisedException("Not authorised to perform this action!");
        }
        DataService.AddToUserAccountBalance(user, amount, token.get());
    }

    @Override
    public void WithdrawFromAccount(IUser user, BigDecimal amount) throws UnauthorisedException{
        Optional<AuthToken> token = AuthService.GetSessionToken(user.GetID());

        if(token.isEmpty() || !token.get().IsAuthed()){
            throw new UnauthorisedException("Not authorised to perform this action!");
        }
        DataService.WithdrawFromUserAccountBalance(user, amount, token.get());
    }

    @Override
    public Customer CreateCustomerAccount(String username, String password, String firstName, String lastName) throws NoSuchElementException, DuplicateUserException{
       return DataService.CreateCustomer(username, password,firstName,lastName);
    }

    @Override
    public Business CreateBusinessAccount(String businessName, String password) throws NoSuchElementException, DuplicateUserException {
        return DataService.CreateBusiness(businessName, password);
    }

    @Override
    public void DeleteUserAccount(IUser user) throws UnauthorisedException {
        Optional<AuthToken> token = AuthService.GetSessionToken(user.GetID());

        if(token.isEmpty() || !token.get().IsAuthed()){
            throw new UnauthorisedException("Not authorised to perform this action!");
        }
        DataService.DeleteUserAccount(user, token.get());
    }

    @Override
    public IUser LoginUser(String username, String password) throws UserNotFoundException, PasswordMismatchException, NoSuchElementException {
        return AuthService.UserLogin(username, password);
    }

    @Override
    public void LogoutUser(IUser user) {

    }
}
