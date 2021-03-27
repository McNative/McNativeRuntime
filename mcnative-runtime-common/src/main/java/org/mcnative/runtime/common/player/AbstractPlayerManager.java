/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.05.20, 21:00
 * @web %web%
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
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.PlayerManager;
import org.mcnative.runtime.api.player.data.MinecraftPlayerData;
import org.mcnative.runtime.api.player.data.PlayerDataProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class AbstractPlayerManager implements PlayerManager {

    private final static CacheQuery<MinecraftPlayer> OFFLINE_PLAYER_BY_ID = new OfflinePlayerByIdQuery();
    private final static CacheQuery<MinecraftPlayer> OFFLINE_PLAYER_BY_XBOX_ID = new OfflinePlayerByXBoxId();
    private final static CacheQuery<MinecraftPlayer> OFFLINE_PLAYER_BY_NAME = new OfflinePlayerByName();

    protected final Map<Class<?>,Function<MinecraftPlayer,?>> adapters;
    protected final Cache<MinecraftPlayer> offlineMinecraftPlayers;

    public AbstractPlayerManager() {
        this.adapters = new LinkedHashMap<>();
        //There is an error in the ShadowArrayCache which caused a server timeout.
        this.offlineMinecraftPlayers = new ArrayCache<>();
        this.offlineMinecraftPlayers.setExpireAfterAccess(10, TimeUnit.MINUTES);
        this.offlineMinecraftPlayers.setMaxSize(512);
    }

    @Override
    public MinecraftPlayer getPlayer(UUID uniqueId) {
        ConnectedMinecraftPlayer online = getConnectedPlayer(uniqueId);
        if(online != null) return online;
        return this.offlineMinecraftPlayers.get(OFFLINE_PLAYER_BY_ID,uniqueId);
    }

    @Override
    public MinecraftPlayer getPlayer(long xBoxId) {
        ConnectedMinecraftPlayer online = getConnectedPlayer(xBoxId);
        if(online != null) return online;
        return this.offlineMinecraftPlayers.get(OFFLINE_PLAYER_BY_XBOX_ID,xBoxId);
    }

    @Override
    public MinecraftPlayer getPlayer(String name) {
        ConnectedMinecraftPlayer online = getConnectedPlayer(name);
        if(online != null) return online;
        return this.offlineMinecraftPlayers.get(OFFLINE_PLAYER_BY_NAME,name);
    }

    @Override
    public <T> void registerPlayerAdapter(Class<T> playerClass, Function<MinecraftPlayer, T> translator) {
        this.adapters.put(playerClass, translator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T translate(Class<T> translatedClass, MinecraftPlayer player) {
        Function<MinecraftPlayer,?> translator = this.adapters.get(translatedClass);
        if(translator == null) throw new IllegalArgumentException(String.format("No translator for player %s class found.",translatedClass));
        return (T) translator.apply(player);
    }


    public abstract ConnectedMinecraftPlayer getConnectedPlayer(UUID uniqueId);

    public abstract  ConnectedMinecraftPlayer getConnectedPlayer(long xBoxId);

    public abstract  ConnectedMinecraftPlayer getConnectedPlayer(String name);


    private static PlayerDataProvider getDataProvider(){
        return McNative.getInstance().getRegistry().getService(PlayerDataProvider.class);
    }

    private static class OfflinePlayerByIdQuery implements CacheQuery<MinecraftPlayer> {

        @Override
        public boolean check(MinecraftPlayer player, Object[] identifiers) {
            return player.getUniqueId().equals(identifiers[0]);
        }

        @Override
        public MinecraftPlayer load(Object[] identifiers) {
            MinecraftPlayerData data = getDataProvider().getPlayerData((UUID) identifiers[0]);
            return data!=null?new OfflineMinecraftPlayer(data):null;
        }
    }

    private static class OfflinePlayerByXBoxId implements CacheQuery<MinecraftPlayer> {

        @Override
        public boolean check(MinecraftPlayer player, Object[] identifiers) {
            return player.getXBoxId() == (long)identifiers[0];
        }

        @Override
        public MinecraftPlayer load(Object[] identifiers) {
            MinecraftPlayerData data = getDataProvider().getPlayerData((long) identifiers[0]);
            return data!=null?new OfflineMinecraftPlayer(data):null;
        }
    }

    private static class OfflinePlayerByName implements CacheQuery<MinecraftPlayer> {

        @Override
        public boolean check(MinecraftPlayer player, Object[] identifiers) {
            return player.getName().equalsIgnoreCase((String) identifiers[0]);
        }

        @Override
        public MinecraftPlayer load(Object[] identifiers) {
            MinecraftPlayerData data = getDataProvider().getPlayerData((String) identifiers[0]);
            return data!=null?new OfflineMinecraftPlayer(data):null;
        }
    }
}
