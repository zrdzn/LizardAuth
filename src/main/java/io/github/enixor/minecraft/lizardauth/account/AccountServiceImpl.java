package io.github.enixor.minecraft.lizardauth.account;

import io.github.enixor.minecraft.lizardauth.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AccountServiceImpl implements AccountService {

    private final Server server;
    private final AccountRepository accountRepository;
    private final SessionManager sessionManager;

    public AccountServiceImpl(Server server, AccountRepository accountRepository, SessionManager sessionManager) {
        this.server = server;
        this.accountRepository = accountRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void registerAccount(UUID playerId, String password, boolean force) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Player with " + playerId + " does not exist.");
        }

        if (this.isRegistered(playerId)) {
            player.sendMessage(Component.text("You are already registered.", NamedTextColor.RED));
            return;
        }

        if (!this.accountRepository.registerAccount(playerId, player.getName(), password)) {
            player.sendMessage(Component.text("Could not register account.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Successfully registered.", NamedTextColor.GREEN));

        if (!this.sessionManager.authorizePlayer(playerId, password, force)) {
            player.kick(Component.text("Something went wrong with auto authenticating, please log in to authenticate manually.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("You have successfully auto authenticated after registering.", NamedTextColor.GREEN));
    }

    @Override
    public void unregisterAccount(UUID playerId, String password, boolean force) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Player with " + playerId + " does not exist.");
        }

        if (!this.sessionManager.authorizePlayer(playerId, password, force)) {
            player.sendMessage(Component.text("Password is incorrect or something went wrong.", NamedTextColor.RED));
            return;
        }

        if (!this.accountRepository.unregisterAccount(playerId, password)) {
            player.sendMessage(Component.text("Could not unregister account.", NamedTextColor.RED));
            return;
        }

        if (force) {
            player.sendMessage(Component.text("You have been unregistered by staff.", NamedTextColor.GOLD));
        }

        player.sendMessage(Component.text("Successfully unregistered.", NamedTextColor.GREEN));

        this.sessionManager.deauthorizePlayer(playerId);
    }

    @Override
    public boolean isRegistered(UUID playerId) {
        return this.accountRepository.isRegistered(playerId);
    }

}
