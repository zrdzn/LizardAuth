package io.github.zrdzn.minecraft.lizardauth.account;

import io.github.zrdzn.minecraft.lizardauth.message.MessageService;
import io.github.zrdzn.minecraft.lizardauth.session.SessionManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final SessionManager sessionManager;
    private final MessageService messageService;

    public AccountServiceImpl(AccountRepository accountRepository, SessionManager sessionManager, MessageService messageService) {
        this.accountRepository = accountRepository;
        this.sessionManager = sessionManager;
        this.messageService = messageService;
    }

    @Override
    public CompletableFuture<Void> registerAccount(UUID playerId, String password, boolean force) {
        return CompletableFuture.runAsync(() -> {
            if (this.accountRepository.isRegistered(playerId)) {
                this.messageService.sendMessage(playerId, "already-registered");
                return;
            }

            if (!this.accountRepository.registerAccount(playerId, null, password)) {
                this.messageService.sendMessage(playerId, "could-not-register");
                return;
            }

            this.messageService.sendMessage(playerId, "successfully-registered");
            this.messageService.sendMessage(playerId, "auto-authorized");
        });
    }

    @Override
    public CompletableFuture<Void> unregisterAccount(UUID playerId, String password, boolean force) {
        return CompletableFuture.runAsync(() -> {
            if (!this.sessionManager.authorizePlayer(playerId, password, force)) {
                this.messageService.sendMessage(playerId, "incorrect-password");
                return;
            }

            if (!this.accountRepository.unregisterAccount(playerId, password)) {
                this.messageService.sendMessage(playerId, "could-not-unregister");
                return;
            }

            if (force) {
                this.messageService.sendMessage(playerId, "force-unregister");
                return;
            }

            this.messageService.sendMessage(playerId, "successfully-unregistered");

            this.sessionManager.deauthorizePlayer(playerId);
        });


    }

    @Override
    public CompletableFuture<Boolean> isRegistered(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> this.accountRepository.isRegistered(playerId));
    }

}
