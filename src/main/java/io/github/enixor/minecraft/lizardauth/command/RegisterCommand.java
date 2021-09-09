package io.github.enixor.minecraft.lizardauth.command;

import io.github.enixor.minecraft.lizardauth.LizardAuthPlugin;
import io.github.enixor.minecraft.lizardauth.account.AccountService;
import io.github.enixor.minecraft.lizardauth.configuration.PasswordSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegisterCommand implements CommandExecutor {

    private final LizardAuthPlugin plugin;
    private final AccountService accountService;

    public RegisterCommand(LizardAuthPlugin plugin, AccountService accountService) {
        this.plugin = plugin;
        this.accountService = accountService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("You need to provide a password.", NamedTextColor.RED));
            return true;
        }

        PasswordSettings passwordSettings = this.plugin.getPasswordSettings();

        int passwordLength = args[0].length();
        int minimumLength = passwordSettings.getMinimumPasswordLength();
        int maximumLength = passwordSettings.getMaximumPasswordLength();

        if (passwordLength < minimumLength) {
            player.sendMessage(Component.text("Password must be longer than " + minimumLength + " characters.", NamedTextColor.RED));
            return true;
        }

        if (passwordLength > maximumLength) {
            player.sendMessage(Component.text("Password must be shorter than " + maximumLength + " characters.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 1) {
            player.sendMessage(Component.text("You need to retype the password.", NamedTextColor.RED));
            return true;
        }

        if (!args[0].equalsIgnoreCase(args[1])) {
            player.sendMessage(Component.text("Password do not match.", NamedTextColor.RED));
            return true;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
                this.accountService.registerAccount(player.getUniqueId(), args[0], false));

        return true;
    }

}
