package org.beaverbrooks.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface IUserAuth {

    Boolean ComparePassword(String password);
    Optional<UUID> GetID();

}
