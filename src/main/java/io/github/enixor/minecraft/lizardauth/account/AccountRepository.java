package io.github.enixor.minecraft.lizardauth.account;

import java.util.UUID;

public interface AccountRepository {

    void registerAccount(UUID playerId, String password, boolean force);

    void unregisterAccount(UUID playerId, String password, boolean force);

    boolean isRegistered(UUID playerId);

    String getHashedPassword(UUID playerId);

}
