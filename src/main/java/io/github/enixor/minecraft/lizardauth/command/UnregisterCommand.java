package io.github.enixor.minecraft.lizardauth.command;

import io.github.enixor.minecraft.lizardauth.LizardAuthPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record UnregisterCommand(LizardAuthPlugin plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("You need to provide a password to verify that it is your account.", NamedTextColor.RED));
            return true;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
                this.plugin.getAccountRepository().unregisterAccount(player.getUniqueId(), args[0], false));

        return true;
    }

}
