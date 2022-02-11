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

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountRepositoryImpl implements AccountRepository {

    private final Logger logger;
    private final HikariDataSource dataSource;

    public AccountRepositoryImpl(HikariDataSource dataSource, Logger logger) {
        this.logger = logger;
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
            this.logger.error("Something went wrong while inserting account.", exception);
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
            this.logger.error("Something went wrong while deleting account.", exception);
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
            this.logger.error("Something went wrong while selecting account.", exception);
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
            this.logger.error("Something went wrong while selecting password from account.", exception);
            return null;
        }
    }

}
