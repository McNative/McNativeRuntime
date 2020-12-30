/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 26.06.20, 21:12
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

package org.mcnative.runtime.network.integrations.cloudnet.v2;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import net.pretronic.libraries.command.manager.CommandManager;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.MinecraftServerType;
import org.mcnative.runtime.api.network.component.server.ServerStatusRequester;
import org.mcnative.runtime.api.network.component.server.ServerStatusResponse;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacket;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNetServer implements MinecraftServer {

    private final ServerInfo info;

    public CloudNetServer(ServerInfo info) {
        this.info = info;
    }

    @Override
    public MinecraftProtocolVersion getProtocolVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(info.getHost(),info.getPort());
    }

    @Override
    public MinecraftServerType getType() {
        ServerGroup group = CloudAPI.getInstance().getServerGroup(info.getServiceId().getGroup());
        if(group.getGroupMode() == ServerGroupMode.LOBBY || group.getGroupMode() == ServerGroupMode.STATIC_LOBBY){
            return MinecraftServerType.FALLBACK;
        }else{
            return MinecraftServerType.NORMAL;
        }
    }

    @Override
    public void setType(MinecraftServerType type) {
        throw new UnsupportedOperationException("It is not possible to modify the server type of a CloudNet server");
    }

    @Override
    public String getPermission() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPermission(String permission) {
        throw new UnsupportedOperationException("It is not possible to modify the permission of a CloudNet server");
    }

    @Override
    public ServerStatusResponse ping() {
        return ServerStatusRequester.getDefault().requestStatus(getAddress());
    }

    @Override
    public CompletableFuture<ServerStatusResponse> pingAsync() {
        return ServerStatusRequester.getDefault().requestStatusAsync(getAddress());
    }

    @Override
    public void sendData(String s, Document document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendData(String s, byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendData(String channel, byte[] data, boolean queued) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return info.getServiceId().getGroup()+"-"+info.getServiceId().getId();
    }

    @Override
    public int getMaxPlayerCount() {
        return info.getMaxPlayers();
    }

    @Override
    public int getOnlineCount() {
        return info.getOnlineCount();
    }

    @Override
    public Collection<OnlineMinecraftPlayer> getOnlinePlayers() {
        Collection<OnlineMinecraftPlayer> result = new ArrayList<>();
        for (String player : info.getPlayers()) {
            result.add(McNative.getInstance().getNetwork().getOnlinePlayer(player));
        }
        return result;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(UUID uniqueId) {
        OnlineMinecraftPlayer player = McNative.getInstance().getNetwork().getOnlinePlayer(uniqueId);
        if(Iterators.findOne(info.getPlayers(), o -> o.equalsIgnoreCase(player.getName())) == null) return null;
        return player;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(String name) {
        if(Iterators.findOne(info.getPlayers(), o -> o.equalsIgnoreCase(name)) != null) {
            return McNative.getInstance().getNetwork().getOnlinePlayer(name);
        }
        return null;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(long xBoxId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void broadcast(MessageComponent<?> component, VariableSet variables) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void broadcast(String permission, MessageComponent<?> component, VariableSet variables) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getGroup() {
        return info.getServiceId().getGroup();
    }

    @Override
    public String getStatus() {
        return info.getServerState().name();
    }

    @Override
    public EventBus getEventBus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandManager getCommandManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOnline() {
        return info.isOnline();
    }

    @Override
    public NetworkIdentifier getIdentifier() {
        return new NetworkIdentifier(getName(),info.getServiceId().getUniqueId());
    }

    @Override
    public void sendMessage(String channel, Document request) {
        McNative.getInstance().getNetwork().getMessenger().sendMessage(this,channel,request);
    }

    @Override
    public CompletableFuture<Document> sendQueryMessageAsync(String channel, Document request) {
        return  McNative.getInstance().getNetwork().getMessenger().sendQueryMessageAsync(this,channel,request);
    }
}
