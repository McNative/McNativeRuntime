/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.10.19, 20:53
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

import net.pretronic.databasequery.api.query.type.UpdateQuery;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.message.language.Language;
import org.mcnative.runtime.api.player.PlayerDesign;
import org.mcnative.runtime.api.player.data.MinecraftPlayerData;
import org.mcnative.runtime.api.player.profile.GameProfile;

import java.util.UUID;

public class DefaultMinecraftPlayerData implements MinecraftPlayerData {

    private transient final DefaultPlayerDataProvider provider;
    private final UUID uniqueId;
    private final long xBoxId;
    private final long firstPlayed;
    private final Document properties;

    private String name;
    private GameProfile gameProfile;
    private PlayerDesign design;
    private Language language;
    private long lastPlayed;

    public DefaultMinecraftPlayerData(DefaultPlayerDataProvider provider, String name, UUID uniqueId, long xBoxId
            , long firstPlayed, long lastPlayed, GameProfile gameProfile,PlayerDesign design,Language language, Document properties) {
        this.provider = provider;
        this.name = name;
        this.uniqueId = uniqueId;
        this.xBoxId = xBoxId;
        this.firstPlayed = firstPlayed;
        this.lastPlayed = lastPlayed;
        this.gameProfile = gameProfile;
        this.design = design;
        this.language = language;
        this.properties = properties;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public long getXBoxId() {
        return this.xBoxId;
    }

    @Override
    public long getFirstPlayed() {
        return this.firstPlayed;
    }

    @Override
    public long getLastPlayed() {
        return this.lastPlayed;
    }

    @Override
    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    @Override
    public PlayerDesign getDesign() {
        return design;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    public void updateLastPlayed(long timeStamp) {
        this.provider.getPlayerDataStorage().update()
                .set("LastPlayed", timeStamp)
                .where("UniqueId", uniqueId)
                .execute();
    }

    @Override
    public void updateName(String name) {
        if(!this.name.equalsIgnoreCase(name)) DefaultPlayerDataProvider.checkSameName(provider,name);
        this.provider.getPlayerDataStorage().update()
                .set("Name", name)
                .where("UniqueId", uniqueId)
                .execute();
        this.name = name;
    }

    @Override
    public void updateGameProfile(GameProfile profile) {
        this.provider.getPlayerDataStorage().update()
                .set("GameProfile",profile.toJsonPart())
                .where("UniqueId", uniqueId)
                .execute();
        this.gameProfile = profile;
    }

    @Override
    public void updateDesign(PlayerDesign design) {
        this.provider.getPlayerDataStorage().update()
                .set("Design", design.toJson())
                .where("UniqueId", uniqueId)
                .execute();
        this.design = design;
    }

    @Override
    public void updateLanguage(Language language) {
        this.provider.getPlayerDataStorage().update()
                .set("Language", language != null ? language.getCode() : null)
                .where("UniqueId", uniqueId)
                .execute();
        this.language = language;
    }

    @Override
    public void updateProperties() {
        this.provider.getPlayerDataStorage().update()
                .set("Properties", DocumentFileType.JSON.getWriter().write(this.properties))
                .where("UniqueId", uniqueId)
                .execute();
    }

    @Override
    public void updateLoginInformation(String name, GameProfile profile, long timeStamp) {
        if(!this.name.equalsIgnoreCase(name)) DefaultPlayerDataProvider.checkSameName(provider,name);
        UpdateQuery query = this.provider.getPlayerDataStorage().update()
                .set("Name", name)
                .set("GameProfile", profile.toJsonPart())
                .set("LastPlayed", timeStamp)
                .where("UniqueId",this.uniqueId);
        if(firstPlayed < 0 ) query.set("FirstPlayed",timeStamp);
        query.execute();
        this.name = name;
        this.gameProfile = profile;
        this.lastPlayed = timeStamp;
    }

    @Override
    public boolean isCached() {
        return true;
    }

    @Override
    public boolean setCached(boolean b) {
        return false;
    }

    @Override
    public MinecraftPlayerData reload() {
        return this;
    }
}
