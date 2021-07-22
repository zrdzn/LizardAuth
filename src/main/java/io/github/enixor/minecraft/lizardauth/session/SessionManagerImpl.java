package io.github.enixor.minecraft.lizardauth.session;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.enixor.minecraft.lizardauth.LizardAuthPlugin;
import io.github.enixor.minecraft.lizardauth.api.account.AccountRepository;
import io.github.enixor.minecraft.lizardauth.api.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManagerImpl implements SessionManager {

    private final Server server;
    private final AccountRepository accountRepository;
    private final Map<UUID, BukkitTask> playerTaskMap = new HashMap<>();

    public SessionManagerImpl(LizardAuthPlugin plugin) {
        this.server = plugin.getServer();
        this.accountRepository = plugin.getAccountRepository();
    }

    @Override
    public Map<UUID, BukkitTask> getPlayerTaskMap() {
        return this.playerTaskMap;
    }

    @Override
    public boolean authorizePlayer(UUID uuid, String password, boolean force) {
        if (!force) {
            Player player = this.server.getPlayer(uuid);
            if (player == null) {
                return false;
            }

            if (this.isPlayerLoggedIn(uuid)) {
                player.sendMessage(Component.text("You are already authorized.", NamedTextColor.RED));
                return false;
            }

            String hashedPassword = this.accountRepository.getHashedPassword(uuid);
            if (hashedPassword == null) {
                player.sendMessage(Component.text("Something went wrong while authenticating.", NamedTextColor.RED));
                return false;
            }

            if (!this.accountRepository.isRegistered(uuid)) {
                player.sendMessage(Component.text("You need to register first.", NamedTextColor.RED));
                return false;
            }

            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword.toCharArray());
            if (!result.verified) {
                player.sendMessage(Component.text("Password is incorrect.", NamedTextColor.RED));
                return false;
            }
        }

        this.playerTaskMap.remove(uuid);

        return true;
    }

    @Override
    public void deauthorizePlayer(UUID uuid) {
        this.playerTaskMap.remove(uuid);
    }

    @Override
    public boolean isPlayerLoggedIn(UUID uuid) {
        return this.server.getOnlinePlayers().contains(this.server.getPlayer(uuid)) && !this.playerTaskMap.containsKey(uuid);
    }

}
