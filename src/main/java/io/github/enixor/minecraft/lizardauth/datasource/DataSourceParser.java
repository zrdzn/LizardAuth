package io.github.enixor.minecraft.lizardauth.datasource;

import org.bukkit.configuration.ConfigurationSection;

public class DataSourceParser {

    public DataSource parse(ConfigurationSection section) {
        String host = section.getString("host", "localhost");

        int port = section.getInt("port", 3306);

        String database = section.getString("database", "minecraft");

        String user = section.getString("user", "root");

        String password = section.getString("password");
        if (password == null) {
            password = "";
        }

        boolean ssl = section.getBoolean("enable-ssl");

        int poolSize = section.getInt("maximum-pool-size", 10);

        long connectionTimeout = section.getLong("connection-timeout");

        return new DataSource(host, port, database, user, password, ssl, poolSize, connectionTimeout);
    }

}
