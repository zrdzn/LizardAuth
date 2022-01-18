package io.github.zrdzn.minecraft.lizardauth.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageServiceImpl implements MessageService {

    private final Map<String, String> rawMessages;
    private final Configuration configuration;
    private final Logger logger;
    private final Server server;

    public MessageServiceImpl(Configuration configuration, Logger logger, Server server) {
        this.rawMessages = new HashMap<>();
        this.configuration = configuration;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void load(ConfigurationSection section) throws InvalidConfigurationException {
        if (!this.configuration.isConfigurationSection(section.getName())) {
            throw new InvalidConfigurationException("Section " + section.getName() + " is not a valid section.");
        }

        section.getKeys(false).forEach(messageKey -> this.rawMessages.put(messageKey, section.getString(messageKey)));
    }

    @Override
    public String getRawString(String key) {
        if (!this.rawMessages.containsKey(key)) {
            this.logger.error("Key {} not found in 'messages' section.", key);
            return "<not_found>";
        }

        return this.rawMessages.get(key);
    }

    @Override
    public Component getComponent(String key, Object... replacements) {
        return MiniMessage.miniMessage().deserialize(this.getRawString(key), TemplateResolver.resolving(replacements));
    }

    @Override
    public void sendMessage(UUID playerId, String key, Object... replacements) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            this.logger.warn("There is not any online player with {} uuid.", playerId);
            return;
        }

        player.sendMessage(this.getComponent(key, replacements));
    }

    @Override
    public void sendMessage(String playerName, String key, Object... replacements) {
        Player player = this.server.getPlayer(playerName);
        if (player == null) {
            this.logger.warn("There is not any online player with {} name.", playerName);
            return;
        }

        player.sendMessage(this.getComponent(key, replacements));
    }

}