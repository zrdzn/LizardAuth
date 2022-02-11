/*
 * Copyright (c) 2022 zrdzn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
