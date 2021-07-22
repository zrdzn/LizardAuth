package io.github.enixor.minecraft.lizardauth.api.account;

import java.util.UUID;

public interface AccountRepository {

    void registerAccount(UUID uuid, String password, boolean force);

    void unregisterAccount(UUID uuid, String password, boolean force);

    boolean isRegistered(UUID uuid);

    String getHashedPassword(UUID uuid);

}
