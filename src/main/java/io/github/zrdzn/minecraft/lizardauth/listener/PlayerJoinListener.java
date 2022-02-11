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
package io.github.zrdzn.minecraft.lizardauth.listener;

import io.github.zrdzn.minecraft.lizardauth.LizardAuthPlugin;
import io.github.zrdzn.minecraft.lizardauth.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final LizardAuthPlugin plugin;
    private final SessionManager sessionManager;

    public PlayerJoinListener(LizardAuthPlugin plugin, SessionManager sessionManager) {
        this.plugin = plugin;
        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        BukkitTask task = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () ->
                        player.sendMessage(Component.text("Login with /login <password>.", NamedTextColor.RED)),
                20L * 2L, 20L * this.plugin.getReminderMessageFrequency());

        this.sessionManager.getPlayerTaskMap().put(playerId, task);
    }

}
