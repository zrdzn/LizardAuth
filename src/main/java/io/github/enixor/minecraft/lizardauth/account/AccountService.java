package io.github.enixor.minecraft.lizardauth.account;

import java.util.UUID;

public interface AccountService {

    void registerAccount(UUID playerId, String password, boolean force);

    void unregisterAccount(UUID playerId, String password, boolean force);

    boolean isRegistered(UUID playerId);

}
