/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.01.20, 14:04
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

package org.mcnative.common.player.profile;

import net.prematic.libraries.caching.ArrayCache;
import net.prematic.libraries.caching.Cache;
import net.prematic.libraries.caching.CacheQuery;
import net.prematic.libraries.document.Document;
import net.prematic.libraries.document.type.DocumentFileType;
import net.prematic.libraries.utility.http.HttpClient;
import net.prematic.libraries.utility.http.HttpResult;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MemoryGameProfileLoader implements GameProfileLoader {

    private static final String MOJANG_PROFILE_BASE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String MOJANG_UUID_BASE_URL = "https://api.mojang.com/users/profiles/minecraft/";

    private final Cache<GameProfile> cache;

    public MemoryGameProfileLoader() {
        this.cache = new ArrayCache<>();
        this.cache.setMaxSize(1000);
        this.cache.setExpireAfterAccess(1,TimeUnit.HOURS);
        this.cache.registerQuery("byUniqueId",new UniqueIdLoader());
    }

    @Override
    public GameProfileInfo getGameProfileInfo(String name) {
        HttpClient client = new HttpClient();
        client.setUrl(MOJANG_UUID_BASE_URL+name);
        HttpResult result = client.connect();
        if(result.getCode() != 200){
            result.close();
            return null;
        }
        Document data = result.getContent(DocumentFileType.JSON.getReader());
        result.close();
        return new GameProfileInfo(data.getString("name"),data.getObject("id",UUID.class));
    }


    @Override
    public GameProfile getGameProfile(UUID uniqueId) {
        return this.cache.get("byUniqueId",uniqueId);
    }


    @Override
    public GameProfile getGameProfileUncached(UUID uniqueId) {
        HttpClient client = new HttpClient();
        client.setUrl(MOJANG_PROFILE_BASE_URL+uniqueId.toString().replace("-",""));
        HttpResult result = client.connect();
        if(result.getCode() != 200){
            result.close();
            return null;
        }
        GameProfile profile =  GameProfile.fromDocument(result.getContent(DocumentFileType.JSON.getReader()));
        result.close();
        this.cache.insert(profile);
        return profile;
    }


    @Override
    public void clearCache() {
        cache.clear();
    }

    private class UniqueIdLoader implements CacheQuery<GameProfile> {

        @Override
        public boolean check(GameProfile o, Object[] objects) {
            return o.getUniqueId().equals(objects[0]);
        }

        @Override
        public GameProfile load(Object[] identifiers) {
            return getGameProfileUncached((UUID) identifiers[0]);
        }
    }
}