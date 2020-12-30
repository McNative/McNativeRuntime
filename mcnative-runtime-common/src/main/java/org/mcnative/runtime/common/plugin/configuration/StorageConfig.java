/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.01.20, 16:11
 *
 * The McNative Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.mcnative.runtime.common.plugin.configuration;

import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.driverloader.PretronicDependencyDriverLoader;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfigBuilder;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveHashMap;
import net.pretronic.libraries.utility.reflect.TypeReference;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.plugin.configuration.Configuration;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;

import java.io.File;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class StorageConfig {

    private final static Type DRIVER_MAP_TYPE = new TypeReference<Map<String, DatabaseDriverConfig<?>>>(){}.getType();
    private final static Type DATABASE_COLLECTION_TYPE = new TypeReference<Collection<DatabaseEntry>>(){}.getType();

    static {
        DatabaseDriverConfig.registerDocumentAdapter();
        DatabaseDriverFactory.setDriverLoader(new PretronicDependencyDriverLoader());
        PretronicDependencyDriverLoader.setDependencyManager(McNative.getInstance().getDependencyManager());
        PretronicDependencyDriverLoader.setClassLoader(dependencyGroup -> dependencyGroup.loadReflected((URLClassLoader) StorageConfig.class.getClassLoader()));
        PretronicDependencyDriverLoader.registerDefaults();
    }

    protected final Map<String, DatabaseDriverConfig<?>> databaseDrivers;
    protected final Collection<DatabaseEntry> databaseEntries;
    private Configuration configuration;

    public StorageConfig(ConfigurationProvider configurationProvider, Configuration configuration) {
        this.configuration = configurationProvider.getConfiguration(McNative.getInstance(), "storage");
        this.databaseDrivers = new CaseIntensiveHashMap<>();
        this.databaseEntries = new ArrayList<>();
        this.configuration = configuration;
    }

    public DatabaseDriverConfig<?> getDriverConfig(String name) {
        DatabaseDriverConfig<?> config = this.databaseDrivers.get(name);
        if(config == null)  throw new IllegalArgumentException("No database driver for name " + name + " found");
        return config.copy();
    }

    public DatabaseEntry getDatabaseEntry(ObjectOwner plugin, String name) {
        DatabaseEntry databaseEntry = Iterators.findOne(this.databaseEntries, entry ->
                entry.pluginName.equalsIgnoreCase(plugin.getName()) && entry.name.equalsIgnoreCase(name));
        if(databaseEntry == null) {
            databaseEntry = Iterators.findOne(this.databaseEntries, entry ->
                    entry.pluginName.equalsIgnoreCase(plugin.getName()) && entry.name.equalsIgnoreCase("default"));
        }
        return databaseEntry;
    }
    public Collection<DatabaseEntry> getDatabaseEntries(Plugin<?> plugin) {
        return Iterators.filter(this.databaseEntries, entry ->
                entry.pluginName.equalsIgnoreCase(plugin.getName()));
    }

    public DatabaseEntry addDatabaseEntry(DatabaseEntry entry) {
        this.databaseEntries.add(entry);
        return entry;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void createDefault() {
        this.databaseDrivers.clear();
        this.databaseEntries.clear();

        this.databaseDrivers.put("default", new SQLDatabaseDriverConfigBuilder()
                .setName("Default")
                .setDialect(Dialect.H2_PORTABLE)
                .setLocation(new File("plugins/McNative/databases/"))
                .build());

        this.databaseDrivers.put("mysql", new SQLDatabaseDriverConfigBuilder()
                .setName("MySQL")
                .setDialect(Dialect.MYSQL)
                .setAddress(new InetSocketAddress("127.0.0.1", 3306))
                .setUsername("McNative")
                .setPassword("masked")
                .build());

        this.databaseEntries.add(new DatabaseEntry("McNative", "default", "McNative", "default"));
        save();
    }

    public StorageConfig load() {
        configuration.load();
        if(configuration.isFirstCreation()){
            createDefault();
        }else{
            this.databaseDrivers.putAll(configuration.getObject("drivers",DRIVER_MAP_TYPE));
            this.databaseEntries.addAll(configuration.getObject("databases",DATABASE_COLLECTION_TYPE));

            if(!databaseDrivers.containsKey("default")){
                this.databaseDrivers.put("default", new SQLDatabaseDriverConfigBuilder()
                        .setName("Default")
                        .setDialect(Dialect.H2_PORTABLE)
                        .setLocation(new File("plugins/McNative/databases/"))
                        .build());
            }
        }
        return this;
    }

    public StorageConfig save() {
        configuration.clear();
        configuration.add("drivers", this.databaseDrivers);
        configuration.add("databases", this.databaseEntries);
        configuration.save();
        return this;
    }

    static class DatabaseEntry {

        final String pluginName;
        final String name;
        final String database;
        final String driverName;

        DatabaseEntry(String pluginName, String name, String database, String driverName) {
            this.pluginName = pluginName;
            this.name = name;
            this.database = database;
            this.driverName = driverName;
        }

        public String getPluginName() {
            return pluginName;
        }

        public String getName() {
            return name;
        }

        public String getDatabase() {
            return database;
        }

        public String getDriverName() {
            return driverName;
        }

        @Override
        public String toString() {
            return "DatabaseEntry{" +
                    "pluginName='" + pluginName + '\'' +
                    ", name='" + name + '\'' +
                    ", database='" + database + '\'' +
                    ", driverName='" + driverName + '\'' +
                    '}';
        }
    }
}
