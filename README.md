FinCore CLI Banking App

This project is a simple command line interface for a mock up of a banking application. The project scope is limited due to current level of knowledge of systems outside of the direct Java environment, such as JDBC and api integrations.

Project Structure

The project follows a Clean Architecture like structure; the main application can be run from the Main class in the app package. Prioritisation has been given to the architecture and design of the project; while the application is simple in scope, and does not require in depth business logic, good architecture is at the forefront of any successful project no matter how large or small.

As such we use the following structure:

app <--- The UI layer that the user interacts with
 |
 |
api <-- A mock api layer, simulates the data and command flow between the app and the backend services but in this particular case is only responsible for authentication
 |
 |
service <-- A service layer containing the complex business logic and also authorisation
 |
 |
repository <-- data repository that provides access to the data. In our case, we are using dummy data loaded in memory at runtime.
 |
 |
domain <-- the domain objects definitions, provides public facing getter methods to interact with the members but otherwise properly encapsulates them privately.

Valiation also plays it's part between the app and api layers but is not itself a separate layer of data flow. Test classes have also been created for each layer, though mocking has not been implemented throughout.

We achieve dependency inversion through a dependency container, allowing us to inject concrete implementations for abstract members into each layer as required. Therefore no module depends on lower level module, but rather both depend on interfaces, which removes the strong coupling and allows for much more flexibility.

Project Interaction

The following actions are available as part of the console app:
- Create customer account
- Create business account
- Log in to customer/business account
- Log out of customer/business account
- Check current balance of own account
- Deposit to own account
- Withdraw from own account

The project is designed to ensure authentication and authorisation are required for any manipulation of the account; in this way, a user is sure to only be able to check, deposit to, and withdraw from, their own account.
