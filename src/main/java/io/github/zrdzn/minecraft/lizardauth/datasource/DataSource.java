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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {

    private final HikariDataSource hikariDataSource;

    public DataSource(String host, int port, String database, String user,
                      String password, boolean ssl, int poolSize, long connectionTimeout) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.setLeakDetectionThreshold(60000L);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000L);

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public HikariDataSource getHikariDataSource() {
        return this.hikariDataSource;
    }

}
