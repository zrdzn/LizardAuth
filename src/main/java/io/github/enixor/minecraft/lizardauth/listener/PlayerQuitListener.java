package io.github.enixor.minecraft.lizardauth.listener;

import io.github.enixor.minecraft.lizardauth.LizardAuthPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuitListener(LizardAuthPlugin plugin) implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getSessionManager().deauthorizePlayer(event.getPlayer().getUniqueId());
    }

}
