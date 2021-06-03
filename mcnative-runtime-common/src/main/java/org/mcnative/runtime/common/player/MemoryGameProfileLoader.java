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

package org.mcnative.runtime.common.player;

import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.http.HttpClient;
import net.pretronic.libraries.utility.http.HttpResult;
import org.mcnative.runtime.api.player.profile.GameProfile;
import org.mcnative.runtime.api.player.profile.GameProfileInfo;
import org.mcnative.runtime.api.player.profile.GameProfileLoader;

import java.net.URI;
import java.util.Base64;
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
        this.cache.registerQuery("byUri", new UriLoader());
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
    public GameProfile getGameProfile(URI uri) {
        return this.cache.get("byUri", uri);
    }

    @Override
    public GameProfile getGameProfileUncached(URI uri) {
        return getGameProfileFromBase64(uri.toString(), getBase64FromUrl(uri));
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    private String getBase64FromUrl(URI uri) {
        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + uri.toString() + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

    private GameProfile getGameProfileFromBase64(String uri, String base64) {
        return new GameProfile(UUID.randomUUID(), uri, new GameProfile.Property[]{new GameProfile.Property("textures", base64, null)});
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

    private class UriLoader implements CacheQuery<GameProfile> {

        @Override
        public boolean check(GameProfile o, Object[] objects) {
            return o.getName().equalsIgnoreCase(objects[0].toString());
        }

        @Override
        public GameProfile load(Object[] identifiers) {
            return getGameProfileUncached((URI) identifiers[0]);
        }
    }
}
