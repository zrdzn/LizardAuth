package io.github.enixor.minecraft.lizardauth.listener;

import io.github.enixor.minecraft.lizardauth.LizardAuthPlugin;
import io.github.enixor.minecraft.lizardauth.api.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public record PlayerJoinListener(LizardAuthPlugin plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SessionManager sessionManager = this.plugin.getSessionManager();

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        BukkitTask task = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () ->
                        player.sendMessage(Component.text("Login with /login <password>.", NamedTextColor.RED)),
                20L * 2L, 20L * this.plugin.getReminderMessageFrequency());

        sessionManager.getPlayerTaskMap().put(playerId, task);
    }

}
