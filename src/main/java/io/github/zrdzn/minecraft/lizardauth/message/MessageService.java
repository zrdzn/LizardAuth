package io.github.zrdzn.minecraft.lizardauth.message;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.UUID;

public interface MessageService {

    /**
     * Loads all messages from specified section to cache.
     *
     * @param section the section that contains keys with messages
     */
    void load(ConfigurationSection section) throws InvalidConfigurationException;

    /**
     * Returns message as raw string.
     *
     * @param key the key from configuration file
     * @return a translated text
     */
    String getRawString(String key);

    /**
     * Returns message as kyori component.
     *
     * @param key          the key from configuration file
     * @param replacements the replacements for optional placeholders
     * @return a message
     * @see <a href="https://docs.adventure.kyori.net/minimessage#placeholder">MiniMessage</a> for placeholders
     */
    Component getComponent(String key, Object... replacements);

    /**
     * Sends message as component to specified player.
     *
     * @param playerId     the id of the player
     * @param key          the key from configuration file
     * @param replacements the replacements for optional placeholders
     * @see <a href="https://docs.adventure.kyori.net/minimessage#placeholder">MiniMessage</a> for placeholders
     */
    void sendMessage(UUID playerId, String key, Object... replacements);

    /**
     * Sends message as component to specified player.
     *
     * @param playerName   the name of the player
     * @param key          the key from configuration file
     * @param replacements the replacements for optional placeholders
     * @see <a href="https://docs.adventure.kyori.net/minimessage#placeholder">MiniMessage</a> for placeholders
     */
    void sendMessage(String playerName, String key, Object... replacements);

}