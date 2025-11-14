package org.beaverbrooks.api;

import org.beaverbrooks.domain.user.*;
import org.beaverbrooks.repository.IDataRepository;
import org.beaverbrooks.repository.InMemoryRepository;
import org.beaverbrooks.service.auth.BasicAuthService;
import org.beaverbrooks.service.auth.IAuthService;
import org.beaverbrooks.service.data.IUserDataService;
import org.beaverbrooks.service.data.UserDataService;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.DuplicateUserException;
import org.beaverbrooks.shared.exceptions.PasswordMismatchException;
import org.beaverbrooks.shared.exceptions.UnauthorisedException;
import org.beaverbrooks.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BankApi class.
 * Tests all API operations including account management, balance operations,
 * and authentication checks.
 */
public class BankApiTest {

    private IDataRepository repository;
    private IAuthService authService;
    private IUserDataService dataService;
    private IBankApi bankApi;
    private UUID testUser1Id;
    private UUID testUser2Id;
    private String testUser1Password;

    /**
     * Sets up test data before each test method.
     * Creates a repository with test users and initializes the API dependencies.
     */
    @BeforeEach
    void SetupRepository() {
        ArrayList<IUser> userData = new ArrayList<>();
        ArrayList<IUserAuth> userAuthData = new ArrayList<>();
        
        testUser1Id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testUser2Id = UUID.fromString("00000000-0000-0000-0000-000000000002");
        
        testUser1Password = "Pa55word!!1234";
        String testUser2Password = "Pa55word!!2234";
        
        User testUser1 = new Customer("TestUser1", "Test", "Tester", testUser1Id);
        User testUser2 = new Customer("TestUser2", "Test", "Tester", testUser2Id);
        
        UserAuth testUserAuth1 = new UserAuth(testUser1Id, testUser1Password);
        UserAuth testUserAuth2 = new UserAuth(testUser2Id, testUser2Password);

        userData.add(testUser1);
        userData.add(testUser2);
        userAuthData.add(testUserAuth1);
        userAuthData.add(testUserAuth2);
        
        repository = new InMemoryRepository(userData, userAuthData);
        authService = new BasicAuthService(repository);
        dataService = new UserDataService(repository, authService);
        bankApi = new BankApi(authService, dataService);
        
        // Create valid token for testUser1
        try {
            authService.CreateUserSession(testUserAuth1);
        } catch (NoSuchElementException e) {
            fail("Failed to set up test");
        }
    }

    /**
     * Tests getting user balance with valid authentication.
     * Verifies that the balance is returned when user is authenticated.
     */
    @Test
    void GetUserBalance_ShouldReturnBalance_WhenUserIsAuthenticated() throws UnauthorisedException, NoSuchElementException {
        // Arrange
        BigDecimal expectedBalance = BigDecimal.valueOf(0.00);
        
        // Act
        BigDecimal result = bankApi.GetUserBalance(testUser1Id);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedBalance, result);
    }

    /**
     * Tests getting user balance without authentication.
     * Verifies that UnauthorisedException is thrown.
     */
    @Test
    void GetUserBalance_ShouldThrowUnauthorisedException_WhenUserNotAuthenticated() {
        // Arrange
        UUID userWithoutSession = testUser2Id;
        
        // Act & Assert
        UnauthorisedException exception = assertThrows(UnauthorisedException.class, () -> {
            bankApi.GetUserBalance(userWithoutSession);
        });
        
        assertEquals("Not authorised to perform this action!", exception.getMessage());
    }

    /**
     * Tests depositing to account with valid authentication.
     * Verifies that the deposit is processed correctly.
     */
    @Test
    void DepositToAccount_ShouldUpdateBalance_WhenUserIsAuthenticated() throws UnauthorisedException {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal initialBalance = repository.GetAccountBalance(testUser1Id);
        BigDecimal depositAmount = BigDecimal.valueOf(100.50);
        BigDecimal expectedBalance = initialBalance.add(depositAmount);
        
        // Act
        bankApi.DepositToAccount(user, depositAmount);
        
        // Assert
        BigDecimal newBalance = repository.GetAccountBalance(testUser1Id);
        assertEquals(expectedBalance, newBalance);
    }

    /**
     * Tests depositing to account without authentication.
     * Verifies that UnauthorisedException is thrown.
     */
    @Test
    void DepositToAccount_ShouldThrowUnauthorisedException_WhenUserNotAuthenticated() {
        // Arrange
        IUser user = repository.GetUserById(testUser2Id).get();
        BigDecimal depositAmount = BigDecimal.valueOf(50.00);
        
        // Act & Assert
        UnauthorisedException exception = assertThrows(UnauthorisedException.class, () -> {
            bankApi.DepositToAccount(user, depositAmount);
        });
        
        assertEquals("Not authorised to perform this action!", exception.getMessage());
    }

    /**
     * Tests withdrawing from account with valid authentication.
     * Verifies that the withdrawal is processed correctly.
     */
    @Test
    void WithdrawFromAccount_ShouldUpdateBalance_WhenUserIsAuthenticated() throws UnauthorisedException {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        // First deposit some money
        BigDecimal initialDeposit = BigDecimal.valueOf(200.00);
        user.GetAccount().get().AddToAccount(initialDeposit);
        repository.SaveUpdateUser(user);
        
        BigDecimal withdrawalAmount = BigDecimal.valueOf(50.25);
        BigDecimal expectedBalance = initialDeposit.subtract(withdrawalAmount);
        
        // Act
        bankApi.WithdrawFromAccount(user, withdrawalAmount);
        
        // Assert
        BigDecimal newBalance = repository.GetAccountBalance(testUser1Id);
        assertEquals(expectedBalance, newBalance);
    }

    /**
     * Tests withdrawing from account without authentication.
     * Verifies that UnauthorisedException is thrown.
     */
    @Test
    void WithdrawFromAccount_ShouldThrowUnauthorisedException_WhenUserNotAuthenticated() {
        // Arrange
        IUser user = repository.GetUserById(testUser2Id).get();
        BigDecimal withdrawalAmount = BigDecimal.valueOf(25.00);
        
        // Act & Assert
        UnauthorisedException exception = assertThrows(UnauthorisedException.class, () -> {
            bankApi.WithdrawFromAccount(user, withdrawalAmount);
        });
        
        assertEquals("Not authorised to perform this action!", exception.getMessage());
    }

    /**
     * Tests creating a customer account.
     * Verifies that the customer is created successfully.
     */
    @Test
    void CreateCustomerAccount_ShouldCreateCustomer_WhenValidDataProvided() throws NoSuchElementException, DuplicateUserException {
        // Arrange
        String username = "NewCustomer";
        String password = "NewPassword123";
        String firstName = "New";
        String lastName = "Customer";
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act
        Customer result = bankApi.CreateCustomerAccount(username, password, firstName, lastName);
        
        // Assert
        assertNotNull(result);
        assertEquals(username, result.GetUsername());
        assertEquals("New Customer", result.GetName());
        assertEquals(initialUserCount + 1, repository.GetAllUsers().size());
        
        // Verify customer was saved
        Optional<IUser> savedUser = repository.GetUserById(result.GetID());
        assertTrue(savedUser.isPresent());
        assertTrue(savedUser.get() instanceof Customer);
    }

    /**
     * Tests creating a customer account with duplicate username.
     * Verifies that DuplicateUserException is thrown.
     */
    @Test
    void CreateCustomerAccount_ShouldThrowDuplicateUserException_WhenUsernameAlreadyExists() {
        // Arrange
        String username = "TestUser1"; // Already exists
        String password = "Password123";
        String firstName = "Test";
        String lastName = "User";
        
        // Act & Assert
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
            bankApi.CreateCustomerAccount(username, password, firstName, lastName);
        });
        
        assertEquals("User already exists", exception.getMessage());
    }

    /**
     * Tests creating a business account.
     * Verifies that the business is created successfully.
     */
    @Test
    void CreateBusinessAccount_ShouldCreateBusiness_WhenValidDataProvided() throws NoSuchElementException, DuplicateUserException {
        // Arrange
        String businessName = "NewBusiness";
        String password = "BusinessPassword123";
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act
        Business result = bankApi.CreateBusinessAccount(businessName, password);
        
        // Assert
        assertNotNull(result);
        assertEquals(businessName, result.GetUsername());
        assertEquals(businessName, result.GetName());
        assertEquals(initialUserCount + 1, repository.GetAllUsers().size());
        
        // Verify business was saved
        Optional<IUser> savedUser = repository.GetUserById(result.GetID());
        assertTrue(savedUser.isPresent());
        assertTrue(savedUser.get() instanceof Business);
    }

    /**
     * Tests creating a business account with duplicate username.
     * Verifies that DuplicateUserException is thrown.
     */
    @Test
    void CreateBusinessAccount_ShouldThrowDuplicateUserException_WhenUsernameAlreadyExists() {
        // Arrange
        String businessName = "TestUser1"; // Already exists
        String password = "BusinessPassword123";
        
        // Act & Assert
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
            bankApi.CreateBusinessAccount(businessName, password);
        });
        
        assertEquals("Business already exists", exception.getMessage());
    }

    /**
     * Tests deleting a user account with valid authentication.
     * Verifies that the account is deleted successfully.
     */
    @Test
    void DeleteUserAccount_ShouldRemoveAccount_WhenUserIsAuthenticated() throws UnauthorisedException {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        int initialUserCount = repository.GetAllUsers().size();
        int initialAuthCount = repository.GetAllUserAuth().size();
        
        // Act
        bankApi.DeleteUserAccount(user);
        
        // Assert
        assertEquals(initialUserCount - 1, repository.GetAllUsers().size());
        assertEquals(initialAuthCount - 1, repository.GetAllUserAuth().size());
        assertFalse(repository.GetAllUsers().contains(user));
        Optional<IUser> deletedUser = repository.GetUserById(testUser1Id);
        assertFalse(deletedUser.isPresent());
    }

    /**
     * Tests deleting a user account without authentication.
     * Verifies that UnauthorisedException is thrown.
     */
    @Test
    void DeleteUserAccount_ShouldThrowUnauthorisedException_WhenUserNotAuthenticated() {
        // Arrange
        IUser user = repository.GetUserById(testUser2Id).get();
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act & Assert
        UnauthorisedException exception = assertThrows(UnauthorisedException.class, () -> {
            bankApi.DeleteUserAccount(user);
        });
        
        assertEquals("Not authorised to perform this action!", exception.getMessage());
        
        // Verify account was not deleted
        assertEquals(initialUserCount, repository.GetAllUsers().size());
        assertTrue(repository.GetAllUsers().contains(user));
    }

    /**
     * Tests user login with valid credentials.
     * Verifies that the user is returned and session is created.
     */
    @Test
    void LoginUser_ShouldReturnUser_WhenValidCredentialsProvided() throws UserNotFoundException, PasswordMismatchException, NoSuchElementException {
        // Arrange
        String username = "TestUser2";
        String password = "Pa55word!!2234";
        
        // Act
        IUser result = bankApi.LoginUser(username, password);
        
        // Assert
        assertNotNull(result);
        assertEquals(username, result.GetUsername());
        assertEquals(testUser2Id, result.GetID());
        
        // Verify session was created
        Optional<AuthToken> sessionToken = authService.GetSessionToken(testUser2Id);
        assertTrue(sessionToken.isPresent());
        assertTrue(sessionToken.get().IsAuthed());
    }

    /**
     * Tests user login with invalid username.
     * Verifies that UserNotFoundException is thrown.
     */
    @Test
    void LoginUser_ShouldThrowUserNotFoundException_WhenUsernameDoesNotExist() {
        // Arrange
        String username = "NonExistentUser";
        String password = "SomePassword123";
        
        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            bankApi.LoginUser(username, password);
        });
        
        assertEquals("User does not exist in the database.", exception.Message);
    }

    /**
     * Tests user login with incorrect password.
     * Verifies that PasswordMismatchException is thrown.
     */
    @Test
    void LoginUser_ShouldThrowPasswordMismatchException_WhenPasswordIsIncorrect() {
        // Arrange
        String username = "TestUser1";
        String incorrectPassword = "WrongPassword123";
        
        // Act & Assert
        PasswordMismatchException exception = assertThrows(PasswordMismatchException.class, () -> {
            bankApi.LoginUser(username, incorrectPassword);
        });
        
        assertEquals("Username or password is incorrect.", exception.Message);
    }

    /**
     * Tests user logout.
     * Note: Current implementation is empty, so this test verifies that behavior.
     */
    @Test
    void LogoutUser_ShouldComplete_WhenCalled() {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        
        // Verify session exists before logout
        Optional<AuthToken> sessionBefore = authService.GetSessionToken(testUser1Id);
        assertTrue(sessionBefore.isPresent());
        
        // Act
        bankApi.LogoutUser(user);
        
        // Assert
        // Note: Current implementation doesn't clear sessions, so session still exists
        Optional<AuthToken> sessionAfter = authService.GetSessionToken(testUser1Id);
        assertTrue(sessionAfter.isPresent()); // Current implementation doesn't clear sessions
    }

    /**
     * Tests multiple balance operations in sequence.
     * Verifies that deposits and withdrawals work correctly together.
     */
    @Test
    void MultipleBalanceOperations_ShouldWorkCorrectly_WhenUserIsAuthenticated() throws UnauthorisedException {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal deposit1 = BigDecimal.valueOf(100.00);
        BigDecimal deposit2 = BigDecimal.valueOf(50.00);
        BigDecimal withdrawal = BigDecimal.valueOf(30.00);
        BigDecimal expectedFinalBalance = deposit1.add(deposit2).subtract(withdrawal);
        
        // Act
        bankApi.DepositToAccount(user, deposit1);
        bankApi.DepositToAccount(user, deposit2);
        bankApi.WithdrawFromAccount(user, withdrawal);
        
        // Assert
        BigDecimal finalBalance = repository.GetAccountBalance(testUser1Id);
        assertEquals(expectedFinalBalance, finalBalance);
    }

    /**
     * Tests getting balance after deposit.
     * Verifies that balance reflects the deposit.
     */
    @Test
    void GetUserBalance_ShouldReflectDeposit_AfterDepositing() throws UnauthorisedException {
        // Arrange
        IUser user = repository.GetUserById(testUser1Id).get();
        BigDecimal depositAmount = BigDecimal.valueOf(75.25);
        BigDecimal expectedBalance = BigDecimal.valueOf(0.00).add(depositAmount);
        
        // Act
        bankApi.DepositToAccount(user, depositAmount);
        BigDecimal balance = bankApi.GetUserBalance(testUser1Id);
        
        // Assert
        assertEquals(expectedBalance, balance);
    }
}
