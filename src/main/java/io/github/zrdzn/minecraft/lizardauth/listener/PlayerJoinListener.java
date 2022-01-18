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
