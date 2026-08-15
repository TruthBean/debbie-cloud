/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;

/**
 * Factory that creates and manages the {@link MongoClient} instance.
 * <p>
 * The client is created from the {@link MongoConfiguration} and reused
 * for the lifetime of the application. Call {@link #close()} to release
 * resources.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MongoClientFactory {

    private final MongoConfiguration configuration;
    private final MongoClient mongoClient;

    public MongoClientFactory(MongoConfiguration configuration) {
        this.configuration = configuration;
        this.mongoClient = createClient(configuration);
    }

    private MongoClient createClient(MongoConfiguration config) {
        var connectionString = config.buildConnectionString();
        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        return MongoClients.create(settings);
    }

    public MongoClient getClient() {
        return mongoClient;
    }

    public MongoConfiguration getConfiguration() {
        return configuration;
    }

    public com.mongodb.client.MongoDatabase getDatabase() {
        var db = configuration.getDatabase();
        if (db == null || db.isBlank()) {
            throw new IllegalStateException("no database configured (debbie.mongo.database)");
        }
        return mongoClient.getDatabase(db);
    }

    public com.mongodb.client.MongoDatabase getDatabase(String name) {
        return mongoClient.getDatabase(name);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}