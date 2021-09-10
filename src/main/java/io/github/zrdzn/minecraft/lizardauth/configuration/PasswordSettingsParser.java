package io.github.zrdzn.minecraft.lizardauth.configuration;

import org.bukkit.configuration.ConfigurationSection;

public class PasswordSettingsParser {

    public PasswordSettings parse(ConfigurationSection section) {
        int minimumPasswordLength = section.getInt("minimum-length", 6);
        int maximumPasswordLength = section.getInt("maximum-length", 64);

        return new PasswordSettings(minimumPasswordLength, maximumPasswordLength);
    }

}
