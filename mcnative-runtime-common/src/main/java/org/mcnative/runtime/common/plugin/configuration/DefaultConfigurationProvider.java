/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.12.19, 19:35
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

import com.zaxxer.hikari.HikariConfig;
import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfig;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.logging.bridge.slf4j.SLF4JStaticBridge;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.interfaces.ShutdownAble;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveHashMap;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveMap;
import net.pretronic.libraries.utility.reflect.ReflectionUtil;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.Setting;
import org.mcnative.runtime.api.plugin.configuration.Configuration;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;

import java.io.File;
import java.util.*;

public class DefaultConfigurationProvider implements ConfigurationProvider, ShutdownAble {

    private final CaseIntensiveMap<DatabaseDriver> databaseDrivers;
    private StorageConfig storageConfig;
    private final DatabaseCollection settings;

    public DefaultConfigurationProvider() {
        this.databaseDrivers = new CaseIntensiveHashMap<>();

        setFallbackSLF4JLogger();

        this.storageConfig = new StorageConfig(this,getConfiguration(McNative.getInstance(), "storage"));
        storageConfig.load();

        migratePlayerSettings();

        this.settings = McNative.getInstance().getRegistry().getService(ConfigurationProvider.class)
                .getDatabase(McNative.getInstance()).createCollection("mcnative_settings")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.INDEX,FieldOption.AUTO_INCREMENT)
                .field("Owner", DataType.STRING,32, FieldOption.NOT_NULL)
                .field("Key", DataType.STRING,64, FieldOption.NOT_NULL)
                .field("Value", DataType.STRING, 1024, FieldOption.NOT_NULL)
                .field("Created", DataType.LONG, FieldOption.NOT_NULL)
                .field("Updated", DataType.LONG, FieldOption.NOT_NULL)
                .create();
    }

    private void migratePlayerSettings() {
        this.settings.find().get("Player").execute();
    }

    private void setFallbackSLF4JLogger() {
        boolean isSet = ReflectionUtil.getFieldValue(HikariConfig.class, "LOGGER") != null;

        if(!isSet) {
            McNative.getInstance().getLogger().info("No SLF4J Logger is set");
            McNative.getInstance().getLogger().info("Trying to set fallback Pretronic SLF4J Logger");
            try {
                SLF4JStaticBridge.setLogger(McNative.getInstance().getLogger());
            } catch (IllegalArgumentException ignored) {
                McNative.getInstance().getLogger().error("Error while setting Pretronic SLF4J Logger");
            }
        }
    }

    @Override
    public File getPluginDataFolder(ObjectOwner owner) {
        return new File("plugins/"+owner.getName()+"/");
    }

    @Override
    public Configuration getConfiguration(ObjectOwner owner, String name) {
        Objects.requireNonNull(owner,name);
        return new FileConfiguration(owner,name,new File("plugins/"+owner.getName()+"/"+name+"."+FileConfiguration.FILE_TYPE.getEnding()));
    }

    @Override
    public Collection<Setting> getSettings(String owner) {
        Collection<Setting> settings = new ArrayList<>();
        this.settings.find().where("Owner", owner).execute().loadIn(settings, this::getSettingFromResultEntry);
        return settings;
    }

    @Override
    public Setting getSetting(String owner, String key) {
        return getSettingFromResultEntry(this.settings.find()
                .where("Owner", owner)
                .where("Key", key)
                .execute().firstOrNull());
    }

    @Override
    public Setting createSetting(String owner, String key, Object value) {
        Validate.notNull(owner);
        Validate.notNull(key);
        Validate.notNull(value);
        long now = System.currentTimeMillis();
        int id = this.settings.insert()
                .set("Owner", owner)
                .set("Key", key)
                .set("Value", value)
                .set("Created", now)
                .set("Updated", now)
                .executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultSetting(id, owner, key, value, now, now);
    }

    @Override
    public void updateSetting(Setting setting) {
        Validate.notNull(setting);
        setting.setUpdated(setting.getUpdated());
        this.settings.update()
                .set("Value", serialize(setting.getValue()))
                .set("Updated", setting.getUpdated())
                .where("Id",setting.getId())
                .execute();
    }

    @Override
    public void deleteSetting(Setting setting) {
        Validate.notNull(setting);
        this.settings.delete()
                .where("Id", setting.getId())
                .execute();
    }

    @Override
    public void deleteSetting(String owner, String key) {
        Setting setting = getSetting(owner, key);
        if(setting == null) return;
        deleteSetting(setting);
    }

    private Setting getSettingFromResultEntry(QueryResultEntry resultEntry) {
        if(resultEntry == null) return null;
        return new DefaultSetting(resultEntry.getInt("Id"),
                resultEntry.getString("Owner"),
                resultEntry.getString("Key"),
                resultEntry.getObject("Value"),
                resultEntry.getLong("Created"),
                resultEntry.getLong("Updated"));
    }

    private String serialize(Object value){
        String result;
        if(value instanceof String) result = (String) value;
        else{
            if(value instanceof Document) result = DocumentFileType.JSON.getWriter().write((Document) value,false);
            else result = value.toString();
        }
        if(result.length() > 1024) throw new IllegalArgumentException("Setting value is to big");
        return result;
    }

    @Override
    public Database getDatabase(ObjectOwner owner, String name) {
        StorageConfig.DatabaseEntry entry = this.storageConfig.getDatabaseEntry(owner, name);
        if(entry != null) {
            DatabaseDriver databaseDriver = getDatabaseDriver(entry.getDriverName());
            if(!databaseDriver.isConnected()) {

                try {
                    databaseDriver.connect();
                }catch (Exception exception){
                    McNative.getInstance().getLogger().error("----------------------------");
                    McNative.getInstance().getLogger().error("[McNative] (Database-Driver) Could not connect to database");
                    McNative.getInstance().getLogger().error("[McNative] (Database-Driver) Plugin: "+owner.getName());
                    McNative.getInstance().getLogger().error("[McNative] (Database-Driver) Name: "+entry.getName());
                    McNative.getInstance().getLogger().error("[McNative] (Database-Driver) Driver: "+entry.getDriverName());
                    McNative.getInstance().getLogger().error("[McNative] (Database-Driver) Database: "+entry.getDatabase());
                    McNative.getInstance().getLogger().error("[McNative] (Database-Driver) Error: "+exception.getMessage());
                    McNative.getInstance().getLogger().error("----------------------------");
                    throw new OperationFailedException("Could not connect to database",exception);
                }

            }
            return databaseDriver.getDatabase(entry.getDatabase());
        }
        throw new IllegalArgumentException("No database found for plugin " + owner.getName() + " and name " + name);
    }

    @Override
    public Database getDatabase(ObjectOwner owner, String name, boolean configCreate) {
        try {
            Database database = getDatabase(owner, name);
            if(database != null) return database;
        } catch (IllegalArgumentException ignored) {}
        if(configCreate) {
            this.storageConfig.addDatabaseEntry(new StorageConfig.DatabaseEntry(owner.getName(),
                    "default", owner.getName(), "default"));
            this.storageConfig.save();
            return getDatabase(owner, name);
        }
        throw new IllegalArgumentException("Can't create or get database for " + owner.getName() + " with name " + name);
    }

    @Override
    public DatabaseDriver getDatabaseDriver(String name) {
        Validate.notNull(name);
        if(!this.databaseDrivers.containsKey(name)) {
            DatabaseDriver driver = DatabaseDriverFactory.create("[McNative] (Database-Driver) "+name, this.storageConfig.getDriverConfig(name),
                    McNative.getInstance().getLogger(), GeneralUtil.getDefaultExecutorService());
            this.databaseDrivers.put(name, driver);
        }
        return this.databaseDrivers.get(name);
    }

    @Override
    public Collection<String> getDatabaseTypes() {
        Collection<String> types = new ArrayList<>();
        for (DatabaseDriver value : this.databaseDrivers.values()) {
            String dialect = "";
            if(value instanceof SQLDatabaseDriver) {
                dialect = "-"+((SQLDatabaseDriver)value).getDialect().getName();
            }

            types.add(value.getConfig().getDriverClass().getSimpleName()+dialect);
        }
        return types;
    }

    @Override
    public Collection<String> getDatabaseTypes(Plugin<?> plugin) {
        Collection<String> types = Iterators.map(storageConfig.getDatabaseEntries(plugin), entry -> {
            String dialect = "";

            DatabaseDriverConfig<?> config = storageConfig.getDriverConfig(entry.driverName);

            if(config instanceof SQLDatabaseDriverConfig) {
                dialect = "-"+((SQLDatabaseDriverConfig<?>)config).getDialect().getName();
            }

            return config.getDriverClass().getSimpleName()+dialect+" ("+config.getName()+")";
        });
        if(types.isEmpty()) return Collections.emptyList();
        return types;
    }

    @Override
    public void shutdown() {
        for (Map.Entry<String, DatabaseDriver> drivers : this.databaseDrivers.entrySet()) {
            if(drivers.getValue().isConnected()){
                drivers.getValue().disconnect();
            }
        }
    }
}
