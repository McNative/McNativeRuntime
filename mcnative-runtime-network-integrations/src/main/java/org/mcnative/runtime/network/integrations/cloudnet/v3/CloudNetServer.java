/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.07.20, 12:03
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

package org.mcnative.runtime.network.integrations.cloudnet.v3;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.player.ServicePlayer;
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
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNetServer implements MinecraftServer {

    private final ServiceInfoSnapshot snapshot;

    public CloudNetServer(ServiceInfoSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public MinecraftProtocolVersion getProtocolVersion() {
        return ping().getVersion().getProtocol();
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(snapshot.getAddress().getHost(),snapshot.getAddress().getPort());
    }

    @Override
    public MinecraftServerType getType() {
        return MinecraftServerType.NORMAL;
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
        return snapshot.getServiceId().getName();
    }

    @Override
    public String getGroup() {
        String[] groups = snapshot.getConfiguration().getGroups();
        return groups.length > 0 ? groups[0] : "NONE";
    }

    @Override
    public String getStatus() {
        return snapshot.getProperty(BridgeServiceProperty.STATE).orElse("OFFLINE");
    }

    @Override
    public int getMaxPlayerCount() {
        return snapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0);
    }

    @Override
    public int getOnlineCount() {
        return snapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
    }

    @Override
    public Collection<OnlineMinecraftPlayer> getOnlinePlayers() {
        Collection<OnlineMinecraftPlayer> result = new ArrayList<>();
        Collection<ServicePlayer> players = snapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
        for (ServicePlayer player : players) {
            result.add(McNative.getInstance().getNetwork().getOnlinePlayer(player.getUniqueId()));
        }
        return result;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(UUID uniqueId) {
        Collection<ServicePlayer> players = snapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
        if(Iterators.findOne(players, servicePlayer -> servicePlayer.getUniqueId().equals(uniqueId)) != null){
            return McNative.getInstance().getNetwork().getOnlinePlayer(uniqueId);
        }
        return null;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(String name) {
        Collection<ServicePlayer> players = snapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
        if(Iterators.findOne(players, servicePlayer -> servicePlayer.getName().equalsIgnoreCase(name)) != null){
            return McNative.getInstance().getNetwork().getOnlinePlayer(name);
        }
        return null;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(long xBoxId) {
        throw new UnsupportedOperationException("Not supported in CloudNet V3");
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
    public EventBus getEventBus() {
        throw new UnsupportedOperationException("Network commands are currently not supported");
    }

    @Override
    public CommandManager getCommandManager() {
        throw new UnsupportedOperationException("Network commands are currently not supported");
    }

    @Override
    public boolean isOnline() {
        return snapshot.isConnected() && snapshot.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false);
    }

    @Override
    public NetworkIdentifier getIdentifier() {
        return new NetworkIdentifier(snapshot.getServiceId().getName(),snapshot.getServiceId().getUniqueId(), getGroup());
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
