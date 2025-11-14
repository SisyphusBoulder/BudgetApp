package org.beaverbrooks.app;

import org.beaverbrooks.api.IBankApi;
import org.beaverbrooks.app.helper.Validation;
import org.beaverbrooks.shared.exceptions.PasswordMismatchException;
import org.beaverbrooks.shared.exceptions.UnauthorisedException;
import org.beaverbrooks.shared.exceptions.UserNotFoundException;
import org.beaverbrooks.shared.exceptions.ValidationException;
import org.beaverbrooks.domain.user.IUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    static void main(String[] args) {

        AppConfig app = new AppConfig();
        IBankApi api = app.GetApi();
        Validation validator = app.GetValidationService();
        Scanner reader = new Scanner(System.in);

        IUser user = null;
        while (true) {
            while (user == null) {
                MainOptionsEnum menuSelectedOption = ShowMainOptions(reader);
                switch (menuSelectedOption) {
                    case MainOptionsEnum.Login:
                        LoginInput loginData = ShowLoginDisplay(reader);
                        try {
                            if (!(validator.ValidateUsername(loginData.Username) && validator.ValidatePassword(loginData.Password))) {
                                System.out.println("Failed to parse username or password");
                            } else {
                                try {
                                    user = api.LoginUser(loginData.Username, loginData.Password);
                                } catch (UserNotFoundException e) {
                                    System.out.println("User Not Found! " + e.Message);
                                } catch (PasswordMismatchException e) {
                                    System.out.println("Password Mismatch! " + e.Message);
                                } catch (Exception e) {
                                    System.out.println("Unknown error! " + e.getMessage());
                                }
                            }
                        } catch (ValidationException e) {
                            System.out.println(e.Message);
                        }
                        break;
                    case MainOptionsEnum.CreateAccount:
                        CustomerInputData newCustomer = CreateCustomerInput(reader);
                        try {
                            if (!(validator.ValidateUsername(newCustomer.Username) && validator.ValidatePassword(newCustomer.Password) && validator.ValidateName(newCustomer.Firstname, newCustomer.Lastname))) {
                                System.out.println("Failed to parse username or password");
                            } else {
                                try {
                                    user = api.CreateCustomerAccount(newCustomer.Username, newCustomer.Password, newCustomer.Firstname, newCustomer.Lastname);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        } catch (ValidationException e) {
                            System.out.println(e.Message);
                        }
                        break;
                    case MainOptionsEnum.CreateBusinessAccount:
                        BusinessInputData newBusiness = CreateBusinessInput(reader);
                        try {
                            if (!(validator.ValidateBusinessName(newBusiness.BusinessName) && validator.ValidatePassword(newBusiness.Password))) {
                                System.out.println("Failed to parse username or password");
                            } else {
                                try {
                                    user = api.CreateBusinessAccount(newBusiness.BusinessName, newBusiness.Password);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        } catch (ValidationException e) {
                            System.out.println(e.Message);
                        }
                        break;
                    case MainOptionsEnum.Exit:
                        System.exit(0);
                    default:
                        System.out.println("Invalid input!");
                }

            }
            while (user != null) {
                BigDecimal initialBalance = BigDecimal.valueOf(0.00);
                try {
                    initialBalance = api.GetUserBalance(user.GetID()).setScale(2, RoundingMode.HALF_EVEN);
                } catch (NoSuchElementException e) {
                    System.out.println("Could not find the user or the account in the database");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                AccountOptionsEnum selectedOption = ShowAccountOptions(reader, user, initialBalance);
                switch (selectedOption) {
                    case AccountOptionsEnum.Balance:
                        try {
                            var balance = api.GetUserBalance(user.GetID());
                            System.out.println("Current Balance: $" + balance.setScale(2, RoundingMode.HALF_EVEN));
                        } catch (NoSuchElementException e) {
                            System.out.println("Could not find the user or the account in the database");
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case AccountOptionsEnum.Deposit:
                        try {
                            BigDecimal amount = EnterValue(reader);
                            api.DepositToAccount(user, amount);
                            System.out.println("Amount deposited: $" + amount);
                            System.out.println("New balance: $" + api.GetUserBalance(user.GetID()));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case AccountOptionsEnum.Withdraw:
                        try {
                            BigDecimal amount = EnterValue(reader);
                            api.WithdrawFromAccount(user, amount);
                            System.out.println("Amount deposited: $" + amount);
                            System.out.println("New balance: $" + api.GetUserBalance(user.GetID()));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case AccountOptionsEnum.Logout:
                        user = null;
                        break;
                    default:
                        System.out.println("Invalid input!");
                }
            }
        }
    }

    private static MainOptionsEnum ShowMainOptions(Scanner reader) {

        System.out.println(
                " ".repeat(10) + "-".repeat(60)
                        + System.lineSeparator()
                        + " ".repeat(20) + "Welcome to the FinCORE CLI Banking App"
                        + System.lineSeparator()
                        + " ".repeat(22) + "Please select an option below"
                        + System.lineSeparator()
                        + " ".repeat(10) + "-".repeat(60)
        );
        int choice = 0;
        while (choice == 0) {
            for (int i = 1; i <= MainOptionsEnum.values().length; i++) {
                System.out.println(i + ". " + MainOptionsEnum.values()[i - 1]);
            }
            choice = Integer.parseInt(reader.nextLine());
            if (choice < 1 || choice > MainOptionsEnum.values().length) {
                System.out.println("Invalid choice");
                choice = 0;
            }
        }
        return MainOptionsEnum.values()[choice - 1];
    }

    private static AccountOptionsEnum ShowAccountOptions(Scanner reader, IUser user, BigDecimal initialBalance) {
        String holderName = user.GetName();
        System.out.println(
                System.lineSeparator()
                        + "=".repeat(8) + " FinCore CLI Banking App " + "=".repeat(8)
                        + System.lineSeparator()
                        + " ".repeat(9) + "Account Holder:" + holderName
                        + System.lineSeparator()
                        + " ".repeat(9) + "Current Balance: " + initialBalance
                        + System.lineSeparator()
                        + "Please select an option from the menu below"
                        + System.lineSeparator()
                        + "=".repeat(40)
        );

        int choice = 0;
        while (choice == 0) {
            System.out.println(
                    System.lineSeparator()
                            + "-".repeat(40)
                            + System.lineSeparator()
                            + " ".repeat(5) + "Welcome: " + holderName
                            + System.lineSeparator()
                            + "Please select an option from the menu below"
                            + System.lineSeparator()
                            + "-".repeat(40)
            );

            for (int i = 1; i <= AccountOptionsEnum.values().length; i++) {
                System.out.println(i + ". " + AccountOptionsEnum.values()[i - 1]);
            }
            choice = Integer.parseInt(reader.nextLine());
            if (choice < 1 || choice > AccountOptionsEnum.values().length) {
                System.out.println("Invalid choice");
                choice = 0;

            }
        }
        return AccountOptionsEnum.values()[choice - 1];
    }


    private static LoginInput ShowLoginDisplay(Scanner reader) {

        System.out.println(
                " ".repeat(10) + "-".repeat(60)
                        + System.lineSeparator()
                        + " ".repeat(15) + "Please enter your username and password to login"
                        + System.lineSeparator()
                        + " ".repeat(10) + "-".repeat(60)
        );
        System.out.println("Username:");
        String username = reader.nextLine();
        System.out.println("Password:");
        String password = reader.nextLine();

        return new LoginInput(username, password);
    }

    private static BigDecimal EnterValue(Scanner reader) throws NumberFormatException {
        System.out.println("Enter the amount:");
        return new BigDecimal(reader.nextLine());


    }

    private static CustomerInputData CreateCustomerInput(Scanner reader) {

        System.out.println("Please enter an account username between 5-20 characters:");
        String username = reader.nextLine();

        System.out.println("Please create a password, between 12 and 64 characters, including at least 2 numbers and special characters:");
        String password = reader.nextLine();

        System.out.println("Please enter your first name:");
        String firstName = reader.nextLine();

        System.out.println("Please enter your surname:");
        String lastName = reader.nextLine();


        return new CustomerInputData(username, password, firstName, lastName);
    }

    private static BusinessInputData CreateBusinessInput(Scanner reader) {

        System.out.println("Please enter your business name between 5-20 characters:");
        String username = reader.nextLine();
        System.out.println("Please create a password, between 12 and 64 characters, including at least 2 numbers and special characters:");
        String password = reader.nextLine();


        return new BusinessInputData(username, password);
    }

    private record LoginInput(String Username, String Password) {
    }

    private record CustomerInputData(String Username, String Password, String Firstname, String Lastname) {
    }

    private record BusinessInputData(String BusinessName, String Password) {
    }

    private enum MainOptionsEnum {
        Login,
        CreateAccount,
        CreateBusinessAccount,
        Exit
    }

    private enum AccountOptionsEnum {
        Balance,
        Deposit,
        Withdraw,
        Logout
    }
}
