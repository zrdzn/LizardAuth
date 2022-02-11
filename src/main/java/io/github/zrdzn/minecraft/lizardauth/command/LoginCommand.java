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

import io.github.zrdzn.minecraft.lizardauth.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class LoginCommand implements CommandExecutor {

    private final SessionManager sessionManager;

    public LoginCommand(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("You need to provide a password to verify that it is your account.", NamedTextColor.RED));
            return true;
        }

        Map<UUID, BukkitTask> playerTaskMap = this.sessionManager.getPlayerTaskMap();
        UUID uuid = player.getUniqueId();

        BukkitTask task = playerTaskMap.get(uuid);
        if (task == null || task.isCancelled()) {
            player.sendMessage(Component.text("You are already logged in.", NamedTextColor.RED));
            return true;
        }

        if (this.sessionManager.authorizePlayer(uuid, args[0], false)) {
            player.sendMessage(Component.text("You have successfully logged in.", NamedTextColor.GREEN));
            return true;
        }

        return true;
    }

}
