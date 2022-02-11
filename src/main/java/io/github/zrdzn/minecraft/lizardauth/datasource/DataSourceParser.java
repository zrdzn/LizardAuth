/*
 * Copyright (c) 2022 zrdzn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.zrdzn.minecraft.lizardauth.datasource;

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
