package io.github.enixor.minecraft.lizardauth.api.session;

import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public interface SessionManager {

    Map<UUID, BukkitTask> getPlayerTaskMap();

    boolean authorizePlayer(UUID uuid, String password, boolean force);

    void deauthorizePlayer(UUID uuid);

    boolean isPlayerLoggedIn(UUID uuid);

}
