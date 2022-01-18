package io.github.zrdzn.minecraft.lizardauth.session;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.zrdzn.minecraft.lizardauth.account.AccountRepository;
import io.github.zrdzn.minecraft.lizardauth.message.MessageService;
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
    private final MessageService messageService;
    private final Map<UUID, BukkitTask> playerTaskMap = new HashMap<>();

    public SessionManagerImpl(Server server, AccountRepository accountRepository, MessageService messageService) {
        this.server = server;
        this.accountRepository = accountRepository;
        this.messageService = messageService;
    }

    @Override
    public Map<UUID, BukkitTask> getPlayerTaskMap() {
        return this.playerTaskMap;
    }

    @Override
    public boolean authorizePlayer(UUID playerId, String password, boolean force) {
        if (!force) {
            Player player = this.server.getPlayer(playerId);
            if (player == null) {
                return false;
            }

            if (this.isPlayerLoggedIn(playerId)) {
                this.messageService.sendMessage(playerId, "already-authorized");
                return false;
            }

            if (!this.accountRepository.isRegistered(playerId)) {
                this.messageService.sendMessage(playerId, "need-to-register-first");
                return false;
            }

            String hashedPassword = this.accountRepository.getHashedPassword(playerId);
            if (hashedPassword == null) {
                this.messageService.sendMessage(playerId, "could-not-authorize");
                return false;
            }

            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword.toCharArray());
            if (!result.verified) {
                player.sendMessage(Component.text("Password is incorrect.", NamedTextColor.RED));
                return false;
            }
        }

        System.out.println(this.playerTaskMap.get(playerId).getTaskId());
        this.playerTaskMap.get(playerId).cancel();
        this.playerTaskMap.remove(playerId);

        return true;
    }

    @Override
    public void deauthorizePlayer(UUID playerId) {
        this.playerTaskMap.remove(playerId);
    }

    @Override
    public boolean isPlayerLoggedIn(UUID playerId) {
        return this.server.getOnlinePlayers().contains(this.server.getPlayer(playerId)) && !this.playerTaskMap.containsKey(playerId);
    }

}
