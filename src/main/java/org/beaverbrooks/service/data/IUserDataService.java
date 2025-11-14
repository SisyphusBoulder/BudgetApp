package org.beaverbrooks.service.data;

import org.beaverbrooks.domain.user.Business;
import org.beaverbrooks.domain.user.Customer;
import org.beaverbrooks.domain.user.IUser;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.DuplicateUserException;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface IUserDataService {

    IUser GetUserData(UUID id);

    IUser GetUserData(String username);

    Customer CreateCustomer(String username, String password, String firstName, String lastName) throws NoSuchElementException, DuplicateUserException;

    Business CreateBusiness(String username, String password) throws NoSuchElementException, DuplicateUserException;

    BigDecimal GetUserAccountBalance(UUID id, AuthToken token);

    void AddToUserAccountBalance(IUser user, BigDecimal amount, AuthToken token);

    void WithdrawFromUserAccountBalance(IUser user, BigDecimal amount, AuthToken token);

    void DeleteUserAccount(IUser user, AuthToken token);
}
