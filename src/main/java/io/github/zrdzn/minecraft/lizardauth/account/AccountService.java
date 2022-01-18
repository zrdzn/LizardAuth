package io.github.zrdzn.minecraft.lizardauth.account;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AccountService {

    CompletableFuture<Void> registerAccount(UUID playerId, String password, boolean force);

    CompletableFuture<Void> unregisterAccount(UUID playerId, String password, boolean force);

    CompletableFuture<Boolean> isRegistered(UUID playerId);

}
