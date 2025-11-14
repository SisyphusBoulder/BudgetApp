package org.beaverbrooks.app;

import org.beaverbrooks.api.BankApi;
import org.beaverbrooks.api.IBankApi;
import org.beaverbrooks.app.helper.Validation;
import org.beaverbrooks.repository.DummyData;
import org.beaverbrooks.repository.InMemoryRepository;
import org.beaverbrooks.repository.IDataRepository;
import org.beaverbrooks.service.auth.BasicAuthService;
import org.beaverbrooks.service.auth.IAuthService;
import org.beaverbrooks.service.data.IUserDataService;
import org.beaverbrooks.service.data.UserDataService;

public class AppConfig {

    private final IDataRepository dataRepository = new InMemoryRepository(DummyData.DummyUserData(), DummyData.DummyAuthData());

    private final IAuthService authService = new BasicAuthService(dataRepository);

    private final Validation validation = new Validation();

    private final IUserDataService dataService = new UserDataService(dataRepository, authService);

    private final IBankApi bankApi = new BankApi(authService, dataService);

    public IAuthService GetAuthService(){
        return authService;
    }

    public Validation GetValidationService(){
        return validation;
    }

    public IBankApi GetApi(){
        return bankApi;
    }
}
