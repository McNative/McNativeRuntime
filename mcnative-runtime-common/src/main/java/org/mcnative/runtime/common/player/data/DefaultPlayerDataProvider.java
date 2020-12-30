/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.10.19, 20:51
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

package org.mcnative.runtime.common.player.data;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.PlayerSetting;
import org.mcnative.runtime.api.player.data.MinecraftPlayerData;
import org.mcnative.runtime.api.player.data.PlayerDataProvider;
import org.mcnative.runtime.api.player.profile.GameProfile;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;
import org.mcnative.runtime.common.player.DefaultPlayerDesign;

import java.util.*;

public class DefaultPlayerDataProvider implements PlayerDataProvider {

    private final DatabaseCollection playerDataStorage;
    private final DatabaseCollection settingsStorage;

    public DefaultPlayerDataProvider() {
        this.playerDataStorage = McNative.getInstance().getRegistry().getService(ConfigurationProvider.class)
                .getDatabase(McNative.getInstance()).createCollection("mcnative_players")
                .field("UniqueId", DataType.UUID, FieldOption.NOT_NULL,FieldOption.UNIQUE, FieldOption.INDEX)
                .field("XBoxId", DataType.LONG, FieldOption.NOT_NULL, FieldOption.INDEX)
                .field("Name", DataType.STRING, 32, FieldOption.NOT_NULL, FieldOption.UNIQUE, FieldOption.INDEX)
                .field("FirstPlayed", DataType.LONG, FieldOption.NOT_NULL)
                .field("LastPlayed", DataType.LONG, FieldOption.NOT_NULL)
                .field("GameProfile", DataType.LONG_TEXT)
                .field("Design", DataType.STRING,500)
                .field("Language", DataType.STRING,10)
                .field("Properties", DataType.LONG_TEXT)
                .create();
        this.settingsStorage = McNative.getInstance().getRegistry().getService(ConfigurationProvider.class)
                .getDatabase(McNative.getInstance()).createCollection("mcnative_settings")
                .field("Id", DataType.INTEGER,FieldOption.PRIMARY_KEY, FieldOption.INDEX,FieldOption.AUTO_INCREMENT)
                .field("Player", DataType.UUID, FieldOption.INDEX, FieldOption.INDEX, FieldOption.NOT_NULL)
                .field("Owner", DataType.STRING,32, FieldOption.NOT_NULL)
                .field("Key", DataType.STRING,64, FieldOption.NOT_NULL)
                .field("Value", DataType.STRING, 1024, FieldOption.NOT_NULL)
                .create();
    }

    public DatabaseCollection getPlayerDataStorage() {
        return playerDataStorage;
    }

    @Override
    public MinecraftPlayerData getPlayerData(String name) {
        Objects.requireNonNull(name);
        return getPlayerDataByQueryResult(this.playerDataStorage.find().where("Name", name).execute());
    }

    @Override
    public MinecraftPlayerData getPlayerData(UUID uniqueId) {
        Objects.requireNonNull(uniqueId);
        return getPlayerDataByQueryResult(this.playerDataStorage.find().where("UniqueId", uniqueId).execute());
    }

    @Override
    public MinecraftPlayerData getPlayerData(long xBoxId) {
        Validate.isTrue(xBoxId == 0,"XBoxId can't be 0");
        return getPlayerDataByQueryResult(this.playerDataStorage.find().where("XBoxId", xBoxId).execute());
    }

    private MinecraftPlayerData getPlayerDataByQueryResult(QueryResult result) {
        if(result.isEmpty()) return null;
        QueryResultEntry entry = result.first();
        try{
            String name = entry.getString("Name");
            UUID uniqueId = entry.getUniqueId("UniqueId");
            String languageCode = entry.getString("Language");

            return new DefaultMinecraftPlayerData(this,
                    name,
                    uniqueId,
                    entry.getLong("XBoxId"),
                    entry.getLong("FirstPlayed"),
                    entry.getLong("LastPlayed"),
                    GameProfile.fromJsonPart(uniqueId,name,entry.getString("GameProfile")),
                    DefaultPlayerDesign.fromJson(entry.getString("Design")),
                    languageCode != null ? Language.getLanguage(languageCode) : null,
                    DocumentFileType.JSON.getReader().read(entry.getString("Properties")));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MinecraftPlayerData createPlayerData(String name, UUID uniqueId, long xBoxId, long firstPlayed, long lastPlayed, GameProfile gameProfile) {
        this.playerDataStorage.insert()
                .set("UniqueId", uniqueId)
                .set("XBoxId", xBoxId)
                .set("Name", name)
                .set("FirstPlayed", firstPlayed)
                .set("LastPlayed", lastPlayed)
                .set("GameProfile", gameProfile == null ? "{}" : gameProfile.toJsonPart())
                .set("Properties","{}")
                .set("Design","{}")
                .execute();
        return new DefaultMinecraftPlayerData(this, name, uniqueId, xBoxId, firstPlayed, lastPlayed, gameProfile,null,null, Document.newDocument());
    }

    @Override
    public Collection<PlayerSetting> loadSettings(UUID uniqueId) {
        QueryResult result = settingsStorage.find().where("Player",uniqueId).execute();
        List<PlayerSetting> settings = new ArrayList<>();
        for (QueryResultEntry entry : result) {
            settings.add(new DefaultPlayerSetting(entry.getInt("Id")
                    ,entry.getString("Owner")
                    ,entry.getString("Key")
                    ,entry.getString("Value")));
        }
        return settings;
    }

    @Override
    public PlayerSetting createSetting(UUID uniqueId, String owner, String key, Object value) {
        Validate.notNull(uniqueId,owner,key,value);
        int id = settingsStorage.insert()
                .set("Player",uniqueId)
                .set("Owner",owner)
                .set("Key",key)
                .set("Value",serialize(value))
                .executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultPlayerSetting(id,owner,key,value);
    }

    @Override
    public void updateSetting(PlayerSetting setting) {
        Validate.notNull(setting);
        settingsStorage.update()
                .set("Value",serialize(setting.getValue()))
                .where("Id",setting.getId())
                .execute();

    }

    @Override
    public void deleteSetting(PlayerSetting setting) {
        Validate.notNull(setting);
        settingsStorage.delete()
                .where("Id",setting.getId())
                .execute();
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
}
