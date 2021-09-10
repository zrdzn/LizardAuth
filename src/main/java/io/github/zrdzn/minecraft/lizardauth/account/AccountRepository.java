package io.github.zrdzn.minecraft.lizardauth.account;

import java.util.UUID;

public interface AccountRepository {

    boolean registerAccount(UUID playerId, String username, String password);

    boolean unregisterAccount(UUID playerId, String password);

    boolean isRegistered(UUID playerId);

    String getHashedPassword(UUID playerId);

}
