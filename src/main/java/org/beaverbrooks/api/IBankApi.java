package org.beaverbrooks.api;

import org.beaverbrooks.domain.user.Business;
import org.beaverbrooks.domain.user.Customer;
import org.beaverbrooks.domain.user.IUser;
import org.beaverbrooks.shared.exceptions.DuplicateUserException;
import org.beaverbrooks.shared.exceptions.PasswordMismatchException;
import org.beaverbrooks.shared.exceptions.UnauthorisedException;
import org.beaverbrooks.shared.exceptions.UserNotFoundException;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface IBankApi {

    BigDecimal GetUserBalance(UUID userID) throws UnauthorisedException;

    void DepositToAccount(IUser user, BigDecimal amount) throws UnauthorisedException;

    void WithdrawFromAccount(IUser user, BigDecimal amount) throws UnauthorisedException;

    Customer CreateCustomerAccount(String username, String password, String firstName, String lastName) throws NoSuchElementException, DuplicateUserException;

    Business CreateBusinessAccount(String businessName, String password) throws NoSuchElementException, DuplicateUserException;

    void DeleteUserAccount(IUser user) throws UnauthorisedException;

    IUser LoginUser(String username, String password) throws UserNotFoundException, PasswordMismatchException;

    void LogoutUser(IUser user) ;
}
