package io.github.enixor.minecraft.lizardauth.command;

import io.github.enixor.minecraft.lizardauth.LizardAuthPlugin;
import io.github.enixor.minecraft.lizardauth.api.session.SessionManager;
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

public record LoginCommand(LizardAuthPlugin plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("You need to provide a password to verify that it is your account.", NamedTextColor.RED));
            return true;
        }

        SessionManager sessionManager = this.plugin.getSessionManager();

        Map<UUID, BukkitTask> playerTaskMap = sessionManager.getPlayerTaskMap();
        UUID uuid = player.getUniqueId();

        BukkitTask task = playerTaskMap.get(uuid);
        if (task == null || task.isCancelled()) {
            player.sendMessage(Component.text("You are already logged in.", NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("You have successfully logged in.", NamedTextColor.GREEN));

        return true;
    }

}
