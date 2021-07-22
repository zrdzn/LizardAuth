package io.github.enixor.minecraft.lizardauth.account;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.zaxxer.hikari.HikariDataSource;
import io.github.enixor.minecraft.lizardauth.api.account.AccountRepository;
import io.github.enixor.minecraft.lizardauth.api.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record AccountRepositoryImpl(Server server, HikariDataSource dataSource, SessionManager sessionManager) implements AccountRepository {

    @Override
    public void registerAccount(UUID playerId, String password, boolean force) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            return;
        }

        if (this.isRegistered(playerId)) {
            player.sendMessage(Component.text("You are already registered.", NamedTextColor.RED));
            return;
        }

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO accounts (uuid, username, password) VALUES (?, ?, ?);")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, player.getName());
            statement.setString(3, BCrypt.withDefaults().hashToString(16, password.toCharArray()));

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
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
            return;
        }

        if (!this.sessionManager.authorizePlayer(playerId, password, force)) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM accounts WHERE uuid = ?;")) {
            statement.setString(1, playerId.toString());

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (force) {
            player.sendMessage(Component.text("You have been unregistered by staff.", NamedTextColor.RED));
        }

        player.sendMessage(Component.text("Successfully unregistered.", NamedTextColor.RED));

        this.sessionManager.deauthorizePlayer(playerId);
    }

    @Override
    public boolean isRegistered(UUID playerId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM accounts WHERE uuid = ?;")) {
            statement.setString(1, playerId.toString());

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public String getHashedPassword(UUID playerId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT password FROM accounts WHERE uuid = ?;")) {
            statement.setString(1, playerId.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet == null || !resultSet.next()) {
                return null;
            }

            return resultSet.getString("password");
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
