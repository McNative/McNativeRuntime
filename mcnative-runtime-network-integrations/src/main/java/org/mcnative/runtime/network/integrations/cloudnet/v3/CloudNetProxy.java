/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.07.20, 11:53
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
import de.dytanic.cloudnet.ext.bridge.ServiceInfoSnapshotUtil;
import net.pretronic.libraries.command.manager.CommandManager;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.component.server.ProxyServer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacket;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNetProxy implements ProxyServer {

    private final ServiceInfoSnapshot snapshot;

    public CloudNetProxy(ServiceInfoSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(snapshot.getAddress().getHost(),snapshot.getAddress().getPort());
    }

    @Override
    public String getName() {
        return snapshot.getServiceId().getName();
    }

    @Override
    public int getMaxPlayerCount() {
        return ServiceInfoSnapshotUtil.getMaxPlayers(snapshot);
    }

    @Override
    public int getOnlineCount() {
        return ServiceInfoSnapshotUtil.getOnlineCount(snapshot);
    }

    @Override
    public Collection<OnlineMinecraftPlayer> getOnlinePlayers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(UUID uniqueId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(String name) {
        throw new UnsupportedOperationException();
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
        String[] groups = snapshot.getConfiguration().getGroups();
        return groups.length > 0 ? groups[0] : "NONE";
    }

    @Override
    public String getStatus() {
        return snapshot.getProperty(BridgeServiceProperty.STATE).orElse("OFFLINE");
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
        return snapshot.isConnected();
    }

    @Override
    public NetworkIdentifier getIdentifier() {
        return new NetworkIdentifier(snapshot.getServiceId().getName(),snapshot.getServiceId().getUniqueId());
    }

    @Override
    public void sendMessage(String channel, Document request) {
        McNative.getInstance().getNetwork().getMessenger().sendMessage(this,channel,request);
    }

    @Override
    public CompletableFuture<Document> sendQueryMessageAsync(String channel, Document request) {
        return McNative.getInstance().getNetwork().getMessenger().sendQueryMessageAsync(this,channel,request);
    }
}
