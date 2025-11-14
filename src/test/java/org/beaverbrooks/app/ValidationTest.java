package org.beaverbrooks.app;

import org.beaverbrooks.app.helper.Validation;
import org.beaverbrooks.shared.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Validation class.
 * Tests all validation methods including username, password, name, and business name validation.
 */
public class ValidationTest {

    private Validation validation;

    /**
     * Sets up test data before each test method.
     * Creates a new Validation instance.
     */
    @BeforeEach
    void SetupValidation() {
        validation = new Validation();
    }

    // ========== ValidateUsername Tests ==========

    /**
     * Tests username validation with valid username.
     * Verifies that valid usernames pass validation.
     */
    @Test
    void ValidateUsername_ShouldReturnTrue_WhenUsernameIsValid() throws ValidationException {
        // Arrange
        String validUsername = "TestUser";
        
        // Act
        boolean result = validation.ValidateUsername(validUsername);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests username validation with valid username containing hyphen.
     * Verifies that hyphens are allowed in usernames.
     */
    @Test
    void ValidateUsername_ShouldReturnTrue_WhenUsernameContainsHyphen() throws ValidationException {
        // Arrange
        String validUsername = "Test-User";
        
        // Act
        boolean result = validation.ValidateUsername(validUsername);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests username validation with minimum valid length (6 characters).
     * Verifies that usernames of length 6 are accepted.
     */
    @Test
    void ValidateUsername_ShouldReturnTrue_WhenUsernameIsMinimumLength() throws ValidationException {
        // Arrange
        String validUsername = "TestUs";
        
        // Act
        boolean result = validation.ValidateUsername(validUsername);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests username validation with maximum valid length (20 characters).
     * Verifies that usernames of length 20 are accepted.
     */
    @Test
    void ValidateUsername_ShouldReturnTrue_WhenUsernameIsMaximumLength() throws ValidationException {
        // Arrange
        String validUsername = "TestUserTestUserTe";
        
        // Act
        boolean result = validation.ValidateUsername(validUsername);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests username validation with empty string.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateUsername_ShouldThrowException_WhenUsernameIsEmpty() {
        // Arrange
        String emptyUsername = "";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateUsername(emptyUsername);
        });
        
        assertEquals("Username was empty", exception.Message);
    }

    /**
     * Tests username validation with blank string (whitespace only).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateUsername_ShouldThrowException_WhenUsernameIsBlank() {
        // Arrange
        String blankUsername = "   ";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateUsername(blankUsername);
        });
        
        assertEquals("Username was empty", exception.Message);
    }

    /**
     * Tests username validation with username that is too short (5 characters).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateUsername_ShouldThrowException_WhenUsernameTooShort() {
        // Arrange
        String shortUsername = "Test1";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateUsername(shortUsername);
        });
        
        assertEquals("Username length incorrect", exception.Message);
    }

    /**
     * Tests username validation with username that is too long (21 characters).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateUsername_ShouldThrowException_WhenUsernameTooLong() {
        // Arrange
        String longUsername = "TestUserTestUserTestU";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateUsername(longUsername);
        });
        
        assertEquals("Username length incorrect", exception.Message);
    }

    /**
     * Tests username validation with illegal characters (numbers).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateUsername_ShouldThrowException_WhenUsernameContainsNumbers() {
        // Arrange
        String usernameWithNumbers = "TestUser123";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateUsername(usernameWithNumbers);
        });
        
        assertEquals("Illegal characters in username", exception.Message);
    }

    /**
     * Tests username validation with illegal characters (special characters).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateUsername_ShouldThrowException_WhenUsernameContainsSpecialChars() {
        // Arrange
        String usernameWithSpecial = "Test_User";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateUsername(usernameWithSpecial);
        });
        
        assertEquals("Illegal characters in username", exception.Message);
    }

    /**
     * Tests username validation with whitespace that gets trimmed.
     * Verifies that leading/trailing whitespace is trimmed before validation.
     */
    @Test
    void ValidateUsername_ShouldTrimWhitespace_WhenUsernameHasWhitespace() throws ValidationException {
        // Arrange
        String usernameWithWhitespace = "  TestUser  ";
        
        // Act
        boolean result = validation.ValidateUsername(usernameWithWhitespace);
        
        // Assert
        assertTrue(result);
    }

    // ========== ValidatePassword Tests ==========

    /**
     * Tests password validation with valid password.
     * Verifies that valid passwords pass validation.
     */
    @Test
    void ValidatePassword_ShouldReturnTrue_WhenPasswordIsValid() throws ValidationException {
        // Arrange
        String validPassword = "ValidPass12!!";
        
        // Act
        boolean result = validation.ValidatePassword(validPassword);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests password validation with minimum valid length (12 characters).
     * Verifies that passwords of length 12 are accepted.
     */
    @Test
    void ValidatePassword_ShouldReturnTrue_WhenPasswordIsMinimumLength() throws ValidationException {
        // Arrange
        String validPassword = "ValidPass12!!";
        
        // Act
        boolean result = validation.ValidatePassword(validPassword);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests password validation with maximum valid length (64 characters).
     * Verifies that passwords of length 64 are accepted.
     */
    @Test
    void ValidatePassword_ShouldReturnTrue_WhenPasswordIsMaximumLength() throws ValidationException {
        // Arrange
        String validPassword = "ValidPass12!!" + "A".repeat(51); // 64 characters total
        
        // Act
        boolean result = validation.ValidatePassword(validPassword);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests password validation with empty string.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordIsEmpty() {
        // Arrange
        String emptyPassword = "";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(emptyPassword);
        });
        
        assertEquals("Password was empty", exception.Message);
    }

    /**
     * Tests password validation with blank string (whitespace only).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordIsBlank() {
        // Arrange
        String blankPassword = "   ";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(blankPassword);
        });
        
        assertEquals("Password was empty", exception.Message);
    }

    /**
     * Tests password validation with password that is too short (11 characters).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordTooShort() {
        // Arrange
        String shortPassword = "ValidPass1!";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(shortPassword);
        });
        
        assertEquals("Password length incorrect", exception.Message);
    }

    /**
     * Tests password validation with password that is too long (65 characters).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordTooLong() {
        // Arrange
        String longPassword = "ValidPass12!!" + "A".repeat(52); // 65 characters total
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(longPassword);
        });
        
        assertEquals("Password length incorrect", exception.Message);
    }

    /**
     * Tests password validation with insufficient digits (only 1 digit).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordHasInsufficientDigits() {
        // Arrange
        String passwordWithOneDigit = "ValidPassword1!!";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(passwordWithOneDigit);
        });
        
        assertEquals("Invalid password signature", exception.Message);
    }

    /**
     * Tests password validation with insufficient special characters (only 1 special).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordHasInsufficientSpecials() {
        // Arrange
        String passwordWithOneSpecial = "ValidPassword12!";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(passwordWithOneSpecial);
        });
        
        assertEquals("Invalid password signature", exception.Message);
    }

    /**
     * Tests password validation with no digits.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordHasNoDigits() {
        // Arrange
        String passwordWithNoDigits = "ValidPassword!!";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(passwordWithNoDigits);
        });
        
        assertEquals("Invalid password signature", exception.Message);
    }

    /**
     * Tests password validation with no special characters.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidatePassword_ShouldThrowException_WhenPasswordHasNoSpecials() {
        // Arrange
        String passwordWithNoSpecials = "ValidPassword12";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidatePassword(passwordWithNoSpecials);
        });
        
        assertEquals("Invalid password signature", exception.Message);
    }

    /**
     * Tests password validation with whitespace that gets trimmed.
     * Verifies that leading/trailing whitespace is trimmed before validation.
     */
    @Test
    void ValidatePassword_ShouldTrimWhitespace_WhenPasswordHasWhitespace() throws ValidationException {
        // Arrange
        String passwordWithWhitespace = "  ValidPass12!!  ";
        
        // Act
        boolean result = validation.ValidatePassword(passwordWithWhitespace);
        
        // Assert
        assertTrue(result);
    }

    // ========== ValidateName Tests ==========

    /**
     * Tests name validation with valid first and last names.
     * Verifies that valid names pass validation.
     */
    @Test
    void ValidateName_ShouldReturnTrue_WhenNamesAreValid() throws ValidationException {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        
        // Act
        boolean result = validation.ValidateName(firstName, lastName);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests name validation with empty first name.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateName_ShouldThrowException_WhenFirstNameIsEmpty() {
        // Arrange
        String firstName = "";
        String lastName = "Doe";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateName(firstName, lastName);
        });
        
        assertEquals("Name was empty", exception.Message);
    }

    /**
     * Tests name validation with empty last name.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateName_ShouldThrowException_WhenLastNameIsEmpty() {
        // Arrange
        String firstName = "John";
        String lastName = "";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateName(firstName, lastName);
        });
        
        assertEquals("Name was empty", exception.Message);
    }

    /**
     * Tests name validation with blank first name.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateName_ShouldThrowException_WhenFirstNameIsBlank() {
        // Arrange
        String firstName = "   ";
        String lastName = "Doe";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateName(firstName, lastName);
        });
        
        assertEquals("Name was empty", exception.Message);
    }

    /**
     * Tests name validation with blank last name.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateName_ShouldThrowException_WhenLastNameIsBlank() {
        // Arrange
        String firstName = "John";
        String lastName = "   ";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateName(firstName, lastName);
        });
        
        assertEquals("Name was empty", exception.Message);
    }

    /**
     * Tests name validation with illegal characters in first name.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateName_ShouldThrowException_WhenFirstNameContainsIllegalChars() {
        // Arrange
        String firstName = "John123";
        String lastName = "Doe";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateName(firstName, lastName);
        });
        
        assertEquals("Name contains illegal characters", exception.Message);
    }

    /**
     * Tests name validation with illegal characters in last name.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateName_ShouldThrowException_WhenLastNameContainsIllegalChars() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe123";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateName(firstName, lastName);
        });
        
        assertEquals("Name contains illegal characters", exception.Message);
    }

    /**
     * Tests name validation with whitespace that gets trimmed.
     * Verifies that leading/trailing whitespace is trimmed before validation.
     */
    @Test
    void ValidateName_ShouldTrimWhitespace_WhenNamesHaveWhitespace() throws ValidationException {
        // Arrange
        String firstName = "  John  ";
        String lastName = "  Doe  ";
        
        // Act
        boolean result = validation.ValidateName(firstName, lastName);
        
        // Assert
        assertTrue(result);
    }

    // ========== ValidateBusinessName Tests ==========

    /**
     * Tests business name validation with valid business name.
     * Verifies that valid business names pass validation.
     */
    @Test
    void ValidateBusinessName_ShouldReturnTrue_WhenBusinessNameIsValid() throws ValidationException {
        // Arrange
        String businessName = "Acme Corporation";
        
        // Act
        boolean result = validation.ValidateBusinessName(businessName);
        
        // Assert
        assertTrue(result);
    }

    /**
     * Tests business name validation with empty string.
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateBusinessName_ShouldThrowException_WhenBusinessNameIsEmpty() {
        // Arrange
        String emptyBusinessName = "";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateBusinessName(emptyBusinessName);
        });
        
        assertEquals("Business name was empty", exception.Message);
    }

    /**
     * Tests business name validation with blank string (whitespace only).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateBusinessName_ShouldThrowException_WhenBusinessNameIsBlank() {
        // Arrange
        String blankBusinessName = "   ";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateBusinessName(blankBusinessName);
        });
        
        assertEquals("Business name was empty", exception.Message);
    }

    /**
     * Tests business name validation with illegal characters (numbers).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateBusinessName_ShouldThrowException_WhenBusinessNameContainsNumbers() {
        // Arrange
        String businessNameWithNumbers = "Acme Corp123";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateBusinessName(businessNameWithNumbers);
        });
        
        assertEquals("Name contains illegal characters", exception.Message);
    }

    /**
     * Tests business name validation with illegal characters (special characters).
     * Verifies that ValidationException is thrown.
     */
    @Test
    void ValidateBusinessName_ShouldThrowException_WhenBusinessNameContainsSpecialChars() {
        // Arrange
        String businessNameWithSpecial = "Acme-Corp";
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validation.ValidateBusinessName(businessNameWithSpecial);
        });
        
        assertEquals("Name contains illegal characters", exception.Message);
    }

    /**
     * Tests business name validation with whitespace that gets trimmed.
     * Verifies that leading/trailing whitespace is trimmed before validation.
     */
    @Test
    void ValidateBusinessName_ShouldTrimWhitespace_WhenBusinessNameHasWhitespace() throws ValidationException {
        // Arrange
        String businessNameWithWhitespace = "  Acme Corporation  ";
        
        // Act
        boolean result = validation.ValidateBusinessName(businessNameWithWhitespace);
        
        // Assert
        assertTrue(result);
    }
}

