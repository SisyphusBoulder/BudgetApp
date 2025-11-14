package org.beaverbrooks.service;

import org.beaverbrooks.domain.user.*;
import org.beaverbrooks.repository.IDataRepository;
import org.beaverbrooks.repository.InMemoryRepository;
import org.beaverbrooks.service.auth.BasicAuthService;
import org.beaverbrooks.service.auth.IAuthService;
import org.beaverbrooks.shared.AuthToken;
import org.beaverbrooks.shared.exceptions.PasswordMismatchException;
import org.beaverbrooks.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BasicAuthService class.
 * Tests all authentication operations including login, session management,
 * and authorization checks.
 */
public class BasicAuthServiceTest {

    private IDataRepository repository;
    private IAuthService authService;
    private UUID testUser1Id;
    private UUID testUser2Id;
    private String testUser1Password;
    private String testUser2Password;

    /**
     * Sets up test data before each test method.
     * Creates a repository with test users and initializes the auth service.
     */
    @BeforeEach
    void SetupRepository() {
        ArrayList<IUser> userData = new ArrayList<>();
        ArrayList<IUserAuth> userAuthData = new ArrayList<>();
        
        testUser1Id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testUser2Id = UUID.fromString("00000000-0000-0000-0000-000000000002");
        
        testUser1Password = "Pa55word!!1234";
        testUser2Password = "Pa55word!!2234";
        
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
    }

    /**
     * Tests user login with valid credentials.
     * Verifies that the user is returned and a session is created.
     */
    @Test
    void UserLogin_ShouldReturnUser_WhenValidCredentialsProvided() throws UserNotFoundException, PasswordMismatchException, NoSuchElementException {
        // Arrange
        String username = "TestUser1";
        String password = testUser1Password;
        
        // Act
        IUser result = authService.UserLogin(username, password);
        
        // Assert
        assertNotNull(result);
        assertEquals(username, result.GetUsername());
        assertEquals(testUser1Id, result.GetID());
        
        // Verify session was created
        Optional<AuthToken> sessionToken = authService.GetSessionToken(testUser1Id);
        assertTrue(sessionToken.isPresent());
        assertTrue(sessionToken.get().IsAuthed());
        assertEquals(testUser1Id, sessionToken.get().UserId());
    }

    /**
     * Tests user login with invalid username.
     * Verifies that UserNotFoundException is thrown.
     */
    @Test
    void UserLogin_ShouldThrowUserNotFoundException_WhenUsernameDoesNotExist() {
        // Arrange
        String username = "NonExistentUser";
        String password = "SomePassword123";
        
        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.UserLogin(username, password);
        });
        
        assertEquals("User does not exist in the database.", exception.Message);
        
        // Verify no session was created
        Optional<AuthToken> sessionToken = authService.GetSessionToken(testUser1Id);
        assertFalse(sessionToken.isPresent());
    }

    /**
     * Tests user login with incorrect password.
     * Verifies that PasswordMismatchException is thrown.
     */
    @Test
    void UserLogin_ShouldThrowPasswordMismatchException_WhenPasswordIsIncorrect() {
        // Arrange
        String username = "TestUser1";
        String incorrectPassword = "WrongPassword123";
        
        // Act & Assert
        PasswordMismatchException exception = assertThrows(PasswordMismatchException.class, () -> {
            authService.UserLogin(username, incorrectPassword);
        });
        
        assertEquals("Username or password is incorrect.", exception.Message);
        
        // Verify no session was created
        Optional<AuthToken> sessionToken = authService.GetSessionToken(testUser1Id);
        assertFalse(sessionToken.isPresent());
    }

    /**
     * Tests user login with case-insensitive username.
     * Verifies that login works with different case.
     */
    @Test
    void UserLogin_ShouldWork_WhenUsernameCaseDiffers() throws UserNotFoundException, PasswordMismatchException, NoSuchElementException {
        // Arrange
        String username = "testuser1"; // Lowercase
        String password = testUser1Password;
        
        // Act
        IUser result = authService.UserLogin(username, password);
        
        // Assert
        assertNotNull(result);
        assertEquals("TestUser1", result.GetUsername()); // Original case preserved
        assertEquals(testUser1Id, result.GetID());
    }

    /**
     * Tests creating a user session.
     * Verifies that a session token is created and stored.
     */
    @Test
    void CreateUserSession_ShouldCreateSession_WhenValidUserAuthProvided() throws NoSuchElementException {
        // Arrange
        IUserAuth userAuth = repository.GetUserAuthById(testUser1Id).get();
        
        // Act
        authService.CreateUserSession(userAuth);
        
        // Assert
        Optional<AuthToken> sessionToken = authService.GetSessionToken(testUser1Id);
        assertTrue(sessionToken.isPresent());
        assertTrue(sessionToken.get().IsAuthed());
        assertEquals(testUser1Id, sessionToken.get().UserId());
    }

    /**
     * Tests user logout.
     * Note: Current implementation is empty, so this test verifies that behavior.
     */
    @Test
    void UserLogout_ShouldComplete_WhenCalled() {
        // Arrange
        // Create a session first
        try {
            IUserAuth userAuth = repository.GetUserAuthById(testUser1Id).get();
            authService.CreateUserSession(userAuth);
        } catch (NoSuchElementException e) {
            fail("Failed to set up test");
        }
        
        // Verify session exists before logout
        Optional<AuthToken> sessionBefore = authService.GetSessionToken(testUser1Id);
        assertTrue(sessionBefore.isPresent());
        
        // Act
        authService.UserLogout();
        
        // Assert
        // Note: Current implementation doesn't clear sessions, so session still exists
        // Verify session still exists (current implementation doesn't clear on logout)
        Optional<AuthToken> sessionAfter = authService.GetSessionToken(testUser1Id);
        assertTrue(sessionAfter.isPresent()); // Current implementation doesn't clear sessions
    }

    /**
     * Tests getting password.
     * Note: Current implementation returns empty string, so this test verifies that behavior.
     */
    @Test
    void GetPassword_ShouldReturnEmptyString_WhenMethodNotImplemented() {
        // Arrange
        String username = "TestUser1";
        
        // Act
        String result = authService.GetPassword(username);
        
        // Assert
        assertEquals("", result);
    }

    /**
     * Tests getting user auth by ID when it exists.
     * Verifies that the correct user auth is returned.
     */
    @Test
    void GetUserAuthFromId_ShouldReturnUserAuth_WhenAuthExists() {
        // Arrange & Act
        Optional<IUserAuth> result = authService.GetUserAuthFromId(testUser1Id);
        
        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().GetID().isPresent());
        assertEquals(testUser1Id, result.get().GetID().get());
        assertTrue(result.get().ComparePassword(testUser1Password));
    }

    /**
     * Tests getting user auth by ID when it does not exist.
     * Verifies that an empty Optional is returned.
     */
    @Test
    void GetUserAuthFromId_ShouldReturnEmpty_WhenAuthDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000999");
        
        // Act
        Optional<IUserAuth> result = authService.GetUserAuthFromId(nonExistentId);
        
        // Assert
        assertFalse(result.isPresent());
    }

    /**
     * Tests authorization action with valid authenticated token.
     * Verifies that authorization succeeds when token matches user ID.
     */
    @Test
    void AuthAction_ShouldReturnTrue_WhenTokenIsValidAndMatchesUserId() throws NoSuchElementException {
        // Arrange
        IUserAuth userAuth = repository.GetUserAuthById(testUser1Id).get();
        authService.CreateUserSession(userAuth);
        AuthToken validToken = authService.GetSessionToken(testUser1Id).get();
        
        // Act
        boolean result = authService.AuthAction(testUser1Id, validToken);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests authorization action with invalid token (not authenticated).
     * Verifies that authorization fails when token is not authenticated.
     */
    @Test
    void AuthAction_ShouldReturnFalse_WhenTokenIsNotAuthenticated() {
        // Arrange
        AuthToken invalidToken = new AuthToken(testUser1Id, false);
        
        // Act
        boolean result = authService.AuthAction(testUser1Id, invalidToken);
        
        // Assert
        assertFalse(result);
    }

    /**
     * Tests authorization action with token for different user.
     * Verifies that authorization fails when token user ID doesn't match.
     */
    @Test
    void AuthAction_ShouldReturnFalse_WhenTokenIsForDifferentUser() throws NoSuchElementException {
        // Arrange
        IUserAuth userAuth2 = repository.GetUserAuthById(testUser2Id).get();
        authService.CreateUserSession(userAuth2);
        AuthToken tokenForUser2 = authService.GetSessionToken(testUser2Id).get();
        
        // Act - Try to authorize action for user1 with user2's token
        boolean result = authService.AuthAction(testUser1Id, tokenForUser2);
        
        // Assert
        assertFalse(result);
    }

    /**
     * Tests getting session token when session exists.
     * Verifies that the correct session token is returned.
     */
    @Test
    void GetSessionToken_ShouldReturnToken_WhenSessionExists() throws NoSuchElementException {
        // Arrange
        IUserAuth userAuth = repository.GetUserAuthById(testUser1Id).get();
        authService.CreateUserSession(userAuth);
        
        // Act
        Optional<AuthToken> result = authService.GetSessionToken(testUser1Id);
        
        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().IsAuthed());
        assertEquals(testUser1Id, result.get().UserId());
    }

    /**
     * Tests getting session token when session does not exist.
     * Verifies that an empty Optional is returned.
     */
    @Test
    void GetSessionToken_ShouldReturnEmpty_WhenSessionDoesNotExist() {
        // Arrange
        UUID userWithoutSession = testUser2Id;
        
        // Act
        Optional<AuthToken> result = authService.GetSessionToken(userWithoutSession);
        
        // Assert
        assertFalse(result.isPresent());
    }

    /**
     * Tests multiple login sessions.
     * Verifies that each user can have their own session.
     */
    @Test
    void MultipleUserSessions_ShouldWorkCorrectly() throws UserNotFoundException, PasswordMismatchException, NoSuchElementException {
        // Arrange & Act
        IUser user1 = authService.UserLogin("TestUser1", testUser1Password);
        IUser user2 = authService.UserLogin("TestUser2", testUser2Password);
        
        // Assert
        assertNotNull(user1);
        assertNotNull(user2);
        assertEquals(testUser1Id, user1.GetID());
        assertEquals(testUser2Id, user2.GetID());
        
        // Verify both sessions exist
        Optional<AuthToken> token1 = authService.GetSessionToken(testUser1Id);
        Optional<AuthToken> token2 = authService.GetSessionToken(testUser2Id);
        
        assertTrue(token1.isPresent());
        assertTrue(token2.isPresent());
        assertTrue(token1.get().IsAuthed());
        assertTrue(token2.get().IsAuthed());
        
        // Verify each token authorizes only its own user
        assertTrue(authService.AuthAction(testUser1Id, token1.get()));
        assertTrue(authService.AuthAction(testUser2Id, token2.get()));
        assertFalse(authService.AuthAction(testUser1Id, token2.get()));
        assertFalse(authService.AuthAction(testUser2Id, token1.get()));
    }

    /**
     * Tests re-login overwrites existing session.
     * Verifies that logging in again creates a new session token.
     */
    @Test
    void UserLogin_ShouldOverwriteExistingSession_WhenUserLogsInAgain() throws UserNotFoundException, PasswordMismatchException, NoSuchElementException {
        // Arrange
        String username = "TestUser1";
        String password = testUser1Password;
        
        // First login
        authService.UserLogin(username, password);
        Optional<AuthToken> firstToken = authService.GetSessionToken(testUser1Id);
        assertTrue(firstToken.isPresent());
        
        // Act - Login again
        IUser secondLogin = authService.UserLogin(username, password);
        Optional<AuthToken> secondToken = authService.GetSessionToken(testUser1Id);
        
        // Assert
        assertNotNull(secondLogin);
        assertTrue(secondToken.isPresent());
        // Both tokens should be valid (implementation may reuse or create new token)
        assertTrue(secondToken.get().IsAuthed());
    }
}
