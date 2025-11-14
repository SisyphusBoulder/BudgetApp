package org.beaverbrooks.domain.user;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserAuth implements IUserAuth {

    private final UUID UserID;
    private final String Password;

    public UserAuth(UUID userID, String password){
        UserID = userID;
        Password = password;
    }

    @Override
    public Boolean ComparePassword(String password) {
        return Objects.equals(this.Password, password);
    }

    @Override
    public Optional<UUID> GetID() {
        return Optional.ofNullable(UserID);
    }


}
