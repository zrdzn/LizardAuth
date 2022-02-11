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
package io.github.zrdzn.minecraft.lizardauth.command;

import io.github.zrdzn.minecraft.lizardauth.LizardAuthPlugin;
import io.github.zrdzn.minecraft.lizardauth.account.AccountService;
import io.github.zrdzn.minecraft.lizardauth.configuration.PasswordSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegisterCommand implements CommandExecutor {

    private final LizardAuthPlugin plugin;
    private final AccountService accountService;

    public RegisterCommand(LizardAuthPlugin plugin, AccountService accountService) {
        this.plugin = plugin;
        this.accountService = accountService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("You need to provide a password.", NamedTextColor.RED));
            return true;
        }

        PasswordSettings passwordSettings = this.plugin.getPasswordSettings();

        int passwordLength = args[0].length();
        int minimumLength = passwordSettings.getMinimumPasswordLength();
        int maximumLength = passwordSettings.getMaximumPasswordLength();

        if (passwordLength < minimumLength) {
            player.sendMessage(Component.text("Password must be longer than " + minimumLength + " characters.", NamedTextColor.RED));
            return true;
        }

        if (passwordLength > maximumLength) {
            player.sendMessage(Component.text("Password must be shorter than " + maximumLength + " characters.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 1) {
            player.sendMessage(Component.text("You need to retype the password.", NamedTextColor.RED));
            return true;
        }

        if (!args[0].equalsIgnoreCase(args[1])) {
            player.sendMessage(Component.text("Password do not match.", NamedTextColor.RED));
            return true;
        }

        this.accountService.registerAccount(player.getUniqueId(), args[0], false);

        return true;
    }

}
