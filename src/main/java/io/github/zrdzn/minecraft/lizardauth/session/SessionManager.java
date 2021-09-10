package io.github.zrdzn.minecraft.lizardauth.session;

import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public interface SessionManager {

    Map<UUID, BukkitTask> getPlayerTaskMap();

    boolean authorizePlayer(UUID playerId, String password, boolean force);

    void deauthorizePlayer(UUID playerId);

    boolean isPlayerLoggedIn(UUID playerId);

}
