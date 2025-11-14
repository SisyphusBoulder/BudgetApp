package org.beaverbrooks.service;

import org.beaverbrooks.domain.user.*;
import org.beaverbrooks.repository.IDataRepository;
import org.beaverbrooks.repository.InMemoryRepository;
import org.beaverbrooks.service.auth.BasicAuthService;
import org.beaverbrooks.service.auth.IAuthService;
import org.beaverbrooks.service.data.IUserDataService;
import org.beaverbrooks.service.data.UserDataService;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.AuthorisationException;
import org.beaverbrooks.shared.exceptions.DuplicateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDataService class.
 * Tests all service operations including user creation, account balance operations,
 * and authorization checks.
 */
public class UserDataServiceTest {

    private IDataRepository repository;
    private IAuthService authService;
    private IUserDataService userDataService;
    private UUID testUser1Id;
    private UUID testUser2Id;
    private AuthToken validToken;
    private AuthToken invalidToken;
    private AuthToken unauthorizedToken;

    /**
     * Sets up test data before each test method.
     * Creates a repository with test users and initializes the service dependencies.
     */
    @BeforeEach
    void SetupRepository() {
        ArrayList<IUser> userData = new ArrayList<>();
        ArrayList<IUserAuth> userAuthData = new ArrayList<>();
        
        testUser1Id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testUser2Id = UUID.fromString("00000000-0000-0000-0000-000000000002");
        
        User testUser1 = new Customer("TestUser1", "Test", "Tester", testUser1Id);
        User testUser2 = new Customer("TestUser2", "Test", "Tester", testUser2Id);
        
        UserAuth testUserAuth1 = new UserAuth(testUser1Id, "Pa55word!!1234");
        UserAuth testUserAuth2 = new UserAuth(testUser2Id, "Pa55word!!2234");

        userData.add(testUser1);
        userData.add(testUser2);
        userAuthData.add(testUserAuth1);
        userAuthData.add(testUserAuth2);
        
        repository = new InMemoryRepository(userData, userAuthData);
        authService = new BasicAuthService(repository);
        userDataService = new UserDataService(repository, authService);
        
        // Create valid token for testUser1
        authService.CreateUserSession(testUserAuth1);
        validToken = authService.GetSessionToken(testUser1Id).get();
        
        // Create invalid token (not authenticated)
        invalidToken = new AuthToken(testUser1Id, false);
        
        // Create unauthorized token (different user)
        UUID differentUserId = UUID.fromString("00000000-0000-0000-0000-000000000999");
        unauthorizedToken = new AuthToken(differentUserId, true);
    }

    /**
     * Tests getting user data by ID.
     * Note: Current implementation returns null, so this test verifies that behavior.
     */
    @Test
    void GetUserData_ById_ShouldReturnNull_WhenMethodNotImplemented() {
        // Arrange & Act
        IUser result = userDataService.GetUserData(testUser1Id);
        
        // Assert
        assertNull(result);
    }

    /**
     * Tests getting user data by username.
     * Note: Current implementation returns null, so this test verifies that behavior.
     */
    @Test
    void GetUserData_ByUsername_ShouldReturnNull_WhenMethodNotImplemented() {
        // Arrange & Act
        IUser result = userDataService.GetUserData("TestUser1");
        
        // Assert
        assertNull(result);
    }

    /**
     * Tests creating a new customer.
     * Verifies that the customer is created, saved to repository, and session is created.
     */
    @Test
    void CreateCustomer_ShouldCreateAndSaveCustomer_WhenValidDataProvided() throws NoSuchElementException, DuplicateUserException {
        // Arrange
        String username = "NewCustomer";
        String password = "NewPassword123";
        String firstName = "New";
        String lastName = "Customer";
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act
        Customer result = userDataService.CreateCustomer(username, password, firstName, lastName);
        
        // Assert
        assertNotNull(result);
        assertEquals(username, result.GetUsername());
        assertEquals("New Customer", result.GetName());
        assertEquals(initialUserCount + 1, repository.GetAllUsers().size());
        
        // Verify customer was saved to repository
        Optional<IUser> savedUser = repository.GetUserById(result.GetID());
        assertTrue(savedUser.isPresent());
        assertInstanceOf(Customer.class, savedUser.get());
        
        // Verify session was created
        Optional<AuthToken> sessionToken = authService.GetSessionToken(result.GetID());
        assertTrue(sessionToken.isPresent());
        assertTrue(sessionToken.get().IsAuthed());
    }

    /**
     * Tests creating a customer with duplicate username.
     * Verifies that DuplicateUserException is thrown when username already exists.
     */
    @Test
    void CreateCustomer_ShouldThrowDuplicateUserException_WhenUsernameAlreadyExists() {
        // Arrange
        String username = "TestUser1"; // Already exists
        String password = "Password123";
        String firstName = "Test";
        String lastName = "User";
        
        // Act & Assert
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
            userDataService.CreateCustomer(username, password, firstName, lastName);
        });
        
        assertEquals("User already exists", exception.getMessage());
        
        // Verify no new user was added
        assertEquals(2, repository.GetAllUsers().size());
    }

    /**
     * Tests creating a new business.
     * Verifies that the business is created, saved to repository, and session is created.
     */
    @Test
    void CreateBusiness_ShouldCreateAndSaveBusiness_WhenValidDataProvided() throws NoSuchElementException, DuplicateUserException {
        // Arrange
        String username = "NewBusiness";
        String password = "BusinessPassword123";
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act
        Business result = userDataService.CreateBusiness(username, password);
        
        // Assert
        assertNotNull(result);
        assertEquals(username, result.GetUsername());
        assertEquals(username, result.GetName());
        assertEquals(initialUserCount + 1, repository.GetAllUsers().size());
        
        // Verify business was saved to repository
        Optional<IUser> savedUser = repository.GetUserById(result.GetID());
        assertTrue(savedUser.isPresent());
        assertInstanceOf(Business.class, savedUser.get());
        
        // Verify session was created
        Optional<AuthToken> sessionToken = authService.GetSessionToken(result.GetID());
        assertTrue(sessionToken.isPresent());
        assertTrue(sessionToken.get().IsAuthed());
    }

    /**
     * Tests creating a business with duplicate username.
     * Verifies that DuplicateUserException is thrown when username already exists (usernames must be unique across all users).
     */
    @Test
    void CreateBusiness_ShouldThrowDuplicateUserException_WhenUsernameAlreadyExists() {
        // Arrange
        String username = "TestUser1"; // Already exists as a customer
        String password = "BusinessPassword123";
        
        // Act & Assert
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
            userDataService.CreateBusiness(username, password);
        });
        
        assertEquals("Business already exists", exception.getMessage());
        
        // Verify no new business was added
        assertEquals(2, repository.GetAllUsers().size());
    }

    /**
     * Tests getting user account balance with valid authorization.
     * Verifies that the balance is returned when the user is authorized.
     */
    @Test
    void GetUserAccountBalance_ShouldReturnBalance_WhenAuthorized() {
        // Arrange
        BigDecimal expectedBalance = BigDecimal.valueOf(0.00);
        
        // Act
        BigDecimal result = userDataService.GetUserAccountBalance(testUser1Id, validToken);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedBalance, result);
    }

    /**
     * Tests getting user account balance with invalid token (not authenticated).
     * Verifies that AuthorisationException is thrown.
     */
    @Test
    void GetUserAccountBalance_ShouldThrowException_WhenTokenNotAuthenticated() {
        // Arrange & Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.GetUserAccountBalance(testUser1Id, invalidToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
    }

    /**
     * Tests getting user account balance with unauthorized token (different user).
     * Verifies that AuthorisationException is thrown.
     */
    @Test
    void GetUserAccountBalance_ShouldThrowException_WhenTokenForDifferentUser() {
        // Arrange & Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.GetUserAccountBalance(testUser1Id, unauthorizedToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
    }

    /**
     * Tests adding to user account balance with valid authorization.
     * Verifies that the balance is updated correctly.
     */
    @Test
    void AddToUserAccountBalance_ShouldUpdateBalance_WhenAuthorized() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal initialBalance = repository.GetAccountBalance(testUser1Id);
        BigDecimal amountToAdd = BigDecimal.valueOf(100.50);
        BigDecimal expectedBalance = initialBalance.add(amountToAdd);
        
        // Act
        userDataService.AddToUserAccountBalance(user, amountToAdd, validToken);
        
        // Assert
        BigDecimal newBalance = repository.GetAccountBalance(testUser1Id);
        assertEquals(expectedBalance, newBalance);
    }

    /**
     * Tests adding to user account balance with invalid token.
     * Verifies that AuthorisationException is thrown.
     */
    @Test
    void AddToUserAccountBalance_ShouldThrowException_WhenTokenNotAuthenticated() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal amountToAdd = BigDecimal.valueOf(50.00);
        
        // Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.AddToUserAccountBalance(user, amountToAdd, invalidToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
    }

    /**
     * Tests adding to user account balance with unauthorized token.
     * Verifies that AuthorisationException is thrown.
     */
    @Test
    void AddToUserAccountBalance_ShouldThrowException_WhenTokenForDifferentUser() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal amountToAdd = BigDecimal.valueOf(50.00);
        
        // Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.AddToUserAccountBalance(user, amountToAdd, unauthorizedToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
    }

    /**
     * Tests withdrawing from user account balance with valid authorization.
     * Verifies that the balance is updated correctly.
     */
    @Test
    void WithdrawFromUserAccountBalance_ShouldUpdateBalance_WhenAuthorized() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        // First add some money to the account
        BigDecimal initialDeposit = BigDecimal.valueOf(200.00);
        user.GetAccount().get().AddToAccount(initialDeposit);
        repository.SaveUpdateUser(user);
        
        BigDecimal amountToWithdraw = BigDecimal.valueOf(50.25);
        BigDecimal expectedBalance = initialDeposit.subtract(amountToWithdraw);
        
        // Act
        userDataService.WithdrawFromUserAccountBalance(user, amountToWithdraw, validToken);
        
        // Assert
        BigDecimal newBalance = repository.GetAccountBalance(testUser1Id);
        assertEquals(expectedBalance, newBalance);
    }

    /**
     * Tests withdrawing from user account balance with invalid token.
     * Verifies that AuthorisationException is thrown.
     */
    @Test
    void WithdrawFromUserAccountBalance_ShouldThrowException_WhenTokenNotAuthenticated() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal amountToWithdraw = BigDecimal.valueOf(25.00);
        
        // Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.WithdrawFromUserAccountBalance(user, amountToWithdraw, invalidToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
    }

    /**
     * Tests withdrawing from user account balance with unauthorized token.
     * Verifies that AuthorisationException is thrown.
     */
    @Test
    void WithdrawFromUserAccountBalance_ShouldThrowException_WhenTokenForDifferentUser() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal amountToWithdraw = BigDecimal.valueOf(25.00);
        
        // Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.WithdrawFromUserAccountBalance(user, amountToWithdraw, unauthorizedToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
    }

    /**
     * Tests deleting a user account with valid authorization.
     * Verifies that the account is removed from the repository.
     */
    @Test
    void DeleteUserAccount_ShouldRemoveAccount_WhenAuthorized() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        int initialUserCount = repository.GetAllUsers().size();
        int initialAuthCount = repository.GetAllUserAuth().size();
        
        // Act
        userDataService.DeleteUserAccount(user, validToken);
        
        // Assert
        assertEquals(initialUserCount - 1, repository.GetAllUsers().size());
        assertEquals(initialAuthCount - 1, repository.GetAllUserAuth().size());
        assertFalse(repository.GetAllUsers().contains(user));
        Optional<IUser> deletedUser = repository.GetUserById(testUser1Id);
        assertFalse(deletedUser.isPresent());
    }

    /**
     * Tests deleting a user account with invalid token.
     * Verifies that AuthorisationException is thrown and account is not deleted.
     */
    @Test
    void DeleteUserAccount_ShouldThrowException_WhenTokenNotAuthenticated() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.DeleteUserAccount(user, invalidToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
        
        // Verify account was not deleted
        assertEquals(initialUserCount, repository.GetAllUsers().size());
        assertTrue(repository.GetAllUsers().contains(user));
    }

    /**
     * Tests deleting a user account with unauthorized token.
     * Verifies that AuthorisationException is thrown and account is not deleted.
     */
    @Test
    void DeleteUserAccount_ShouldThrowException_WhenTokenForDifferentUser() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act & Assert
        AuthorisationException exception = assertThrows(AuthorisationException.class, () -> {
            userDataService.DeleteUserAccount(user, unauthorizedToken);
        });
        
        assertEquals("Not authorised to perform this action", exception.getMessage());
        
        // Verify account was not deleted
        assertEquals(initialUserCount, repository.GetAllUsers().size());
        assertTrue(repository.GetAllUsers().contains(user));
    }

    /**
     * Tests multiple balance operations in sequence.
     * Verifies that multiple deposits and withdrawals work correctly.
     */
    @Test
    void MultipleBalanceOperations_ShouldWorkCorrectly_WhenAuthorized() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal deposit1 = BigDecimal.valueOf(100.00);
        BigDecimal deposit2 = BigDecimal.valueOf(50.00);
        BigDecimal withdrawal = BigDecimal.valueOf(30.00);
        BigDecimal expectedFinalBalance = deposit1.add(deposit2).subtract(withdrawal);
        
        // Act
        userDataService.AddToUserAccountBalance(user, deposit1, validToken);
        userDataService.AddToUserAccountBalance(user, deposit2, validToken);
        userDataService.WithdrawFromUserAccountBalance(user, withdrawal, validToken);
        
        // Assert
        BigDecimal finalBalance = repository.GetAccountBalance(testUser1Id);
        assertEquals(expectedFinalBalance, finalBalance);
    }
}
