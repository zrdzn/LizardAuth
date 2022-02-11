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
package io.github.zrdzn.minecraft.lizardauth;

import com.zaxxer.hikari.HikariDataSource;
import io.github.zrdzn.minecraft.lizardauth.account.AccountRepository;
import io.github.zrdzn.minecraft.lizardauth.account.AccountService;
import io.github.zrdzn.minecraft.lizardauth.account.AccountServiceImpl;
import io.github.zrdzn.minecraft.lizardauth.message.MessageService;
import io.github.zrdzn.minecraft.lizardauth.message.MessageServiceImpl;
import io.github.zrdzn.minecraft.lizardauth.session.SessionManager;
import io.github.zrdzn.minecraft.lizardauth.command.LoginCommand;
import io.github.zrdzn.minecraft.lizardauth.command.RegisterCommand;
import io.github.zrdzn.minecraft.lizardauth.command.UnregisterCommand;
import io.github.zrdzn.minecraft.lizardauth.configuration.PasswordSettings;
import io.github.zrdzn.minecraft.lizardauth.account.AccountRepositoryImpl;
import io.github.zrdzn.minecraft.lizardauth.configuration.PasswordSettingsParser;
import io.github.zrdzn.minecraft.lizardauth.datasource.DataSourceParser;
import io.github.zrdzn.minecraft.lizardauth.session.SessionManagerImpl;
import io.github.zrdzn.minecraft.lizardauth.listener.PlayerJoinListener;
import io.github.zrdzn.minecraft.lizardauth.listener.PlayerQuitListener;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LizardAuthPlugin extends JavaPlugin {

    private HikariDataSource dataSource;
    private PasswordSettings passwordSettings;
    private long reminderMessageFrequency;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        Logger logger = this.getSLF4JLogger();

        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();
        Configuration configuration = this.getConfig();

        ConfigurationSection databaseSection = configuration.getConfigurationSection("database");
        if (databaseSection == null) {
            logger.error("Section database does not exist.");
            pluginManager.disablePlugin(this);
            return;
        }

        DataSourceParser dataSourceParser = new DataSourceParser();
        this.dataSource = dataSourceParser.parse(databaseSection).getHikariDataSource();

        if (this.dataSource == null) {
            logger.error("Something went wrong while connecting to database. Check your database configuration and restart your server after correcting it.");
            pluginManager.disablePlugin(this);
            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS accounts (" +
                "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "uuid VARCHAR(36) NOT NULL UNIQUE KEY," +
                "username VARCHAR(16) NOT NULL," +
                "password VARCHAR(128) NOT NULL);";
        try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Something went wrong while creating 'accounts' table.", exception);
            pluginManager.disablePlugin(this);
            return;
        }

        PasswordSettingsParser passwordSettingsParser = new PasswordSettingsParser();
        this.passwordSettings = passwordSettingsParser.parse(configuration.getConfigurationSection("password"));

        this.reminderMessageFrequency = configuration.getLong("reminder-message-frequency", 5L);

        AccountRepository accountRepository = new AccountRepositoryImpl(this.getDataSource(), logger);

        MessageService messageService = new MessageServiceImpl(configuration, logger, server);
        try {
            logger.info("Loading messages from configuration file.");
            messageService.load(configuration.getConfigurationSection("messages"));
        } catch (InvalidConfigurationException exception) {
            logger.error("Section 'messages' does not exist.", exception);
            pluginManager.disablePlugin(this);
            return;
        }

        SessionManager sessionManager = new SessionManagerImpl(server, accountRepository, messageService);

        AccountService accountService = new AccountServiceImpl(accountRepository, sessionManager, messageService);

        this.getCommand("register").setExecutor(new RegisterCommand(this, accountService));
        this.getCommand("unregister").setExecutor(new UnregisterCommand(accountService));
        this.getCommand("login").setExecutor(new LoginCommand(sessionManager));

        pluginManager.registerEvents(new PlayerJoinListener(this, sessionManager), this);
        pluginManager.registerEvents(new PlayerQuitListener(sessionManager), this);
    }

    @Override
    public void onDisable() {
        this.dataSource.close();
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public PasswordSettings getPasswordSettings() {
        return this.passwordSettings;
    }

    public long getReminderMessageFrequency() {
        return this.reminderMessageFrequency;
    }

}