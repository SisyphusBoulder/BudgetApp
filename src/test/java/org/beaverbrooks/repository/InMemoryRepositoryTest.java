package org.beaverbrooks.repository;

import org.beaverbrooks.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryRepository class.
 * Tests all repository operations including CRUD operations and account balance retrieval.
 */
public class InMemoryRepositoryTest {

    private IDataRepository repository;
    private UUID testUser1Id;
    private UUID testUser2Id;
    private UUID testUser3Id;

    /**
     * Sets up test data before each test method.
     * Creates three test users with corresponding authentication data.
     */
    @BeforeEach
    void SetupRepository(){
        ArrayList<IUser> userData = new ArrayList<>();
        ArrayList<IUserAuth> userAuthData = new ArrayList<>();
        
        testUser1Id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testUser2Id = UUID.fromString("00000000-0000-0000-0000-000000000002");
        testUser3Id = UUID.fromString("00000000-0000-0000-0000-000000000003");
        
        User testUser1 = new Customer("TestUser1", "Test", "Tester", testUser1Id);
        User testUser2 = new Customer("TestUser2", "Test", "Tester", testUser2Id);
        User testUser3 = new Customer("TestUser3", "Test", "Tester", testUser3Id);
        
        UserAuth testUserAuth1 = new UserAuth(testUser1Id, "Pa55word!!1234");
        UserAuth testUserAuth2 = new UserAuth(testUser2Id, "Pa55word!!2234");
        UserAuth testUserAuth3 = new UserAuth(testUser3Id, "Pa55word!!3234");

        userData.add(testUser1);
        userData.add(testUser2);
        userData.add(testUser3);
        userAuthData.add(testUserAuth1);
        userAuthData.add(testUserAuth2);
        userAuthData.add(testUserAuth3);
        
        repository = new InMemoryRepository(userData, userAuthData);
    }

    /**
     * Tests finding a user by name.
     * Verifies that the correct user is returned when searching by username.
     */
    @Test
    void FindByName_ShouldReturnUser_WhenUserExists() {
        // Arrange & Act
        Optional<IUser> result = repository.FindByName("TestUser1");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("TestUser1", result.get().GetUsername());
        assertEquals(testUser1Id, result.get().GetID());
    }

    /**
     * Tests finding a user by name with case-insensitive matching.
     * Verifies that the search is case-insensitive.
     */
    @Test
    void FindByName_ShouldReturnUser_WhenCaseDiffers() {
        // Arrange & Act
        Optional<IUser> result = repository.FindByName("testuser1");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("TestUser1", result.get().GetUsername());
    }

    /**
     * Tests finding a user by name when the user does not exist.
     * Verifies that an empty Optional is returned.
     */
    @Test
    void FindByName_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange & Act
        Optional<IUser> result = repository.FindByName("NonExistentUser");
        
        // Assert
        assertFalse(result.isPresent());
    }

    /**
     * Tests saving a new user and authentication data.
     * Verifies that both user and auth data are added to the repository.
     */
    @Test
    void SaveNewUser_ShouldAddUserAndAuth_WhenValidDataProvided() {
        // Arrange
        UUID newUserId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        IUser newUser = new Customer("NewUser", "New", "User", newUserId);
        IUserAuth newUserAuth = new UserAuth(newUserId, "NewPassword123");
        int initialUserCount = repository.GetAllUsers().size();
        int initialAuthCount = repository.GetAllUserAuth().size();
        
        // Act
        repository.SaveNewUser(newUserAuth, newUser);
        
        // Assert
        assertEquals(initialUserCount + 1, repository.GetAllUsers().size());
        assertEquals(initialAuthCount + 1, repository.GetAllUserAuth().size());
        assertTrue(repository.GetAllUsers().contains(newUser));
        assertTrue(repository.GetAllUserAuth().contains(newUserAuth));
        
        Optional<IUser> retrievedUser = repository.GetUserById(newUserId);
        assertTrue(retrievedUser.isPresent());
        assertEquals("NewUser", retrievedUser.get().GetUsername());
    }

    /**
     * Tests updating an existing user.
     * Note: Current implementation adds the user again rather than updating.
     * This test verifies the current behavior.
     */
    @Test
    void SaveUpdateUser_ShouldAddUser_WhenCalled() {
        // Arrange
        UUID existingUserId = testUser1Id;
        IUser updatedUser = new Customer("UpdatedUser1", "Updated", "Name", existingUserId);
        int initialUserCount = repository.GetAllUsers().size();
        
        // Act
        repository.SaveUpdateUser(updatedUser);
        
        // Assert
        // Note: Current implementation adds rather than updates, so count increases
        assertEquals(initialUserCount + 1, repository.GetAllUsers().size());
        assertTrue(repository.GetAllUsers().contains(updatedUser));
    }

    /**
     * Tests retrieving a user by ID when the user exists.
     * Verifies that the correct user is returned.
     */
    @Test
    void GetUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange & Act
        Optional<IUser> result = repository.GetUserById(testUser1Id);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("TestUser1", result.get().GetUsername());
        assertEquals(testUser1Id, result.get().GetID());
        assertEquals("Test Tester", result.get().GetName());
    }

    /**
     * Tests retrieving a user by ID when the user does not exist.
     * Verifies that an empty Optional is returned.
     */
    @Test
    void GetUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000999");
        
        // Act
        Optional<IUser> result = repository.GetUserById(nonExistentId);
        
        // Assert
        assertFalse(result.isPresent());
    }

    /**
     * Tests retrieving user authentication data by ID when it exists.
     * Verifies that the correct authentication data is returned.
     */
    @Test
    void GetUserAuthById_ShouldReturnUserAuth_WhenAuthExists() {
        // Arrange & Act
        Optional<IUserAuth> result = repository.GetUserAuthById(testUser1Id);
        
        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().GetID().isPresent());
        assertEquals(testUser1Id, result.get().GetID().get());
        assertTrue(result.get().ComparePassword("Pa55word!!1234"));
        assertFalse(result.get().ComparePassword("WrongPassword"));
    }

    /**
     * Tests retrieving user authentication data by ID when it does not exist.
     * Verifies that an empty Optional is returned.
     */
    @Test
    void GetUserAuthById_ShouldReturnEmpty_WhenAuthDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000999");
        
        // Act
        Optional<IUserAuth> result = repository.GetUserAuthById(nonExistentId);
        
        // Assert
        assertFalse(result.isPresent());
    }

    /**
     * Tests retrieving account balance for a user.
     * Verifies that the balance is correctly retrieved and matches expected initial value.
     */
    @Test
    void GetAccountBalanceForUser_ShouldReturnBalance_WhenUserExists() {
        // Arrange
        BigDecimal expectedBalance = BigDecimal.valueOf(0.00);
        
        // Act
        BigDecimal user1Balance = repository.GetAccountBalance(testUser1Id);
        BigDecimal user2Balance = repository.GetAccountBalance(testUser2Id);
        BigDecimal user3Balance = repository.GetAccountBalance(testUser3Id);
        
        // Assert
        assertNotNull(user1Balance);
        assertNotNull(user2Balance);
        assertNotNull(user3Balance);
        assertEquals(expectedBalance, user1Balance);
        assertEquals(expectedBalance, user2Balance);
        assertEquals(expectedBalance, user3Balance);
    }

    /**
     * Tests deleting an account.
     * Verifies that both user and authentication data are removed from the repository.
     */
    @Test
    void DeleteAccount_ShouldRemoveAccountFromDataStructure() {
        // Arrange
        IUser testUser1 = repository.GetUserById(testUser1Id).orElseThrow();
        IUserAuth testUserAuth1 = repository.GetUserAuthById(testUser1Id).orElseThrow();
        int initialUserCount = repository.GetAllUsers().size();
        int initialAuthCount = repository.GetAllUserAuth().size();
        
        // Act
        repository.DeleteAccount(testUser1);
        
        // Assert
        assertFalse(repository.GetAllUsers().contains(testUser1));
        assertFalse(repository.GetAllUserAuth().contains(testUserAuth1));
        assertEquals(initialUserCount - 1, repository.GetAllUsers().size());
        assertEquals(initialAuthCount - 1, repository.GetAllUserAuth().size());
        assertEquals(2, repository.GetAllUsers().size());
        assertEquals(2, repository.GetAllUserAuth().size());
    }

    /**
     * Tests getting all users.
     * Verifies that all users in the repository are returned.
     */
    @Test
    void GetAllUsers_ShouldReturnAllUsers() {
        // Arrange & Act
        ArrayList<IUser> allUsers = repository.GetAllUsers();
        
        // Assert
        assertNotNull(allUsers);
        assertEquals(3, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.GetUsername().equals("TestUser1")));
        assertTrue(allUsers.stream().anyMatch(u -> u.GetUsername().equals("TestUser2")));
        assertTrue(allUsers.stream().anyMatch(u -> u.GetUsername().equals("TestUser3")));
    }

    /**
     * Tests getting all user authentication data.
     * Verifies that all authentication records in the repository are returned.
     */
    @Test
    void GetAllUserAuth_ShouldReturnAllUserAuth() {
        // Arrange & Act
        ArrayList<IUserAuth> allUserAuth = repository.GetAllUserAuth();
        
        // Assert
        assertNotNull(allUserAuth);
        assertEquals(3, allUserAuth.size());
        assertTrue(allUserAuth.stream().anyMatch(auth -> 
            auth.GetID().isPresent() && auth.GetID().get().equals(testUser1Id)));
        assertTrue(allUserAuth.stream().anyMatch(auth -> 
            auth.GetID().isPresent() && auth.GetID().get().equals(testUser2Id)));
        assertTrue(allUserAuth.stream().anyMatch(auth -> 
            auth.GetID().isPresent() && auth.GetID().get().equals(testUser3Id)));
    }
}
