package io.github.enixor.minecraft.lizardauth.account;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountRepositoryImpl implements AccountRepository {

    private final HikariDataSource dataSource;

    public AccountRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean registerAccount(UUID playerId, String username, String password) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO accounts (uuid, username, password) VALUES (?, ?, ?);")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, username);
            statement.setString(3, BCrypt.withDefaults().hashToString(16, password.toCharArray()));

            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unregisterAccount(UUID playerId, String password) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM accounts WHERE uuid = ?;")) {
            statement.setString(1, playerId.toString());

            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
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
