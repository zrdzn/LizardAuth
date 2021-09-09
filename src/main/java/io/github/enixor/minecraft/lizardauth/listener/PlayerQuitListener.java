package io.github.enixor.minecraft.lizardauth.listener;

import io.github.enixor.minecraft.lizardauth.session.SessionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final SessionManager sessionManager;

    public PlayerQuitListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.sessionManager.deauthorizePlayer(event.getPlayer().getUniqueId());
    }

}
