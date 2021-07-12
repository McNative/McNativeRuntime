/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 06.04.20, 09:55
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

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.syncproxy.AbstractSyncProxyManagement;
import de.dytanic.cloudnet.ext.syncproxy.configuration.SyncProxyMotd;
import de.dytanic.cloudnet.wrapper.Wrapper;
import net.pretronic.libraries.command.manager.CommandManager;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.synchronisation.NetworkSynchronisationCallback;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.Network;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.NetworkOperations;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.ProxyServer;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.common.network.event.NetworkEventBus;
import org.mcnative.runtime.network.integrations.McNativeGlobalExecutor;
import org.mcnative.runtime.network.integrations.SmartLeaderElector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CloudNetV3Network implements Network {

    private final CloudNetV3Messenger messenger;
    private final NetworkOperations operations;
    private final NetworkIdentifier localIdentifier;
    private final NetworkIdentifier networkIdentifier;
    private final NetworkEventBus eventBus;
    private final SmartLeaderElector leaderElector;

    public CloudNetV3Network(Executor executor) {
        this.messenger = new CloudNetV3Messenger(executor);
        this.operations = new CloudNetV3NetworkOperations(this);
        this.localIdentifier = new NetworkIdentifier(
                Wrapper.getInstance().getServiceId().getName()
                ,Wrapper.getInstance().getServiceId().getUniqueId());
        this.networkIdentifier = new NetworkIdentifier(getName(),new UUID(0,0));
        this.eventBus = new NetworkEventBus();
        this.leaderElector = new SmartLeaderElector(this);

        this.messenger.registerChannel("mcnative_event", ObjectOwner.SYSTEM,eventBus);
        this.messenger.registerChannel(SmartLeaderElector.CHANNEL, ObjectOwner.SYSTEM,this.leaderElector);

        VariableDescriberRegistry.registerDescriber(CloudNetServer.class);
        VariableDescriberRegistry.registerDescriber(CloudNetProxy.class);

        this.leaderElector.detectCurrentLeader();
    }

    @Override
    public String getTechnology() {
        return "CloudNet V3";
    }

    @Override
    public CloudNetV3Messenger getMessenger() {
        return messenger;
    }

    @Override
    public NetworkOperations getOperations() {
        return operations;
    }

    @Override
    public boolean isConnected() {
        return messenger.isAvailable();
    }

    @Override
    public EventBus getEventBus() {
       return eventBus;
    }

    @Override
    public CommandManager getCommandManager() {
        throw new UnsupportedOperationException("Network command manager is currently not integrated");
    }

    @Override
    public NetworkIdentifier getLocalIdentifier() {
        return localIdentifier;
    }

    @Override
    public NetworkIdentifier getIdentifier(String name) {
        ServiceInfoSnapshot service = Wrapper.getInstance().getCloudServiceProvider().getCloudServiceByName(name);
        if(service == null) return null;//throw new OperationFailedException("Server is not registered in cloud");
        return new NetworkIdentifier(name,service.getServiceId().getUniqueId());
    }

    @Override
    public NetworkIdentifier getIdentifier(UUID uuid) {
        ServiceInfoSnapshot service = Wrapper.getInstance().getCloudServiceProvider().getCloudService(uuid);
        if(service == null) return null;//throw new OperationFailedException("Server is not registered in cloud");
        return new NetworkIdentifier(service.getName(),service.getServiceId().getUniqueId());
    }

    @Override
    public Collection<ProxyServer> getProxies() {
        Collection<ServiceEnvironmentType> types = new ArrayList<>();
        for (ServiceEnvironmentType value : ServiceEnvironmentType.values()) {
            if(value.isMinecraftJavaProxy() || value.isMinecraftBedrockProxy()) types.add(value);
        }
        Collection<ServiceInfoSnapshot> snapshots = new ArrayList<>();
        for (ServiceEnvironmentType type : types) {
            snapshots.addAll(Wrapper.getInstance().getCloudServiceProvider().getCloudServices(type));
        }

        Collection<ProxyServer> servers = new ArrayList<>();
        for (ServiceInfoSnapshot snapshot : snapshots) {
            servers.add(new CloudNetProxy(snapshot));
        }
        return servers;
    }

    @Override
    public Collection<ProxyServer> getProxies(String s) {
        return null;
    }

    @Override
    public ProxyServer getProxy(String name) {
        ServiceInfoSnapshot snapshot = Wrapper.getInstance().getCloudServiceProvider().getCloudServiceByName(name);
        ServiceEnvironmentType environment = snapshot.getConfiguration().getServiceId().getEnvironment();
        if(environment.isMinecraftJavaProxy() || environment.isMinecraftBedrockProxy()){
            return new CloudNetProxy(snapshot);
        }
        return null;
    }

    @Override
    public ProxyServer getProxy(UUID uniqueId) {
        ServiceInfoSnapshot snapshot = Wrapper.getInstance().getCloudServiceProvider().getCloudService(uniqueId);
        ServiceEnvironmentType environment = snapshot.getConfiguration().getServiceId().getEnvironment();
        if(environment.isMinecraftJavaProxy() || environment.isMinecraftBedrockProxy()){
            return new CloudNetProxy(snapshot);
        }
        return null;
    }

    @Override
    public ProxyServer getLeaderProxy() {
        return getProxy(leaderElector.getLeader());
    }

    @Override
    public boolean isLeaderProxy(ProxyServer proxyServer) {
        return proxyServer.getUniqueId().equals(leaderElector.getLeader());
    }

    @Override
    public Collection<MinecraftServer> getServers() {
        Collection<ServiceEnvironmentType> types = new ArrayList<>();
        for (ServiceEnvironmentType value : ServiceEnvironmentType.values()) {
            if(value.isMinecraftJavaServer() || value.isMinecraftBedrockServer()) types.add(value);
        }
        Collection<ServiceInfoSnapshot> snapshots = new ArrayList<>();
        for (ServiceEnvironmentType type : types) {
            snapshots.addAll(Wrapper.getInstance().getCloudServiceProvider().getCloudServices(type));
        }

        Collection<MinecraftServer> servers = new ArrayList<>();
        for (ServiceInfoSnapshot snapshot : snapshots) {
            servers.add(new CloudNetServer(snapshot));
        }
        return servers;
    }

    @Override
    public Collection<MinecraftServer> getServers(String s) {
        return null;
    }

    @Override
    public MinecraftServer getServer(String name) {
        ServiceInfoSnapshot snapshot = Wrapper.getInstance().getCloudServiceProvider().getCloudServiceByName(name);
        ServiceEnvironmentType environment = snapshot.getConfiguration().getServiceId().getEnvironment();
        if(environment.isMinecraftJavaServer() || environment.isMinecraftBedrockServer()){
            return new CloudNetServer(snapshot);
        }
        return null;
    }

    @Override
    public MinecraftServer getServer(UUID uniqueId) {
        ServiceInfoSnapshot snapshot = Wrapper.getInstance().getCloudServiceProvider().getCloudService(uniqueId);
        ServiceEnvironmentType environment = snapshot.getConfiguration().getServiceId().getEnvironment();
        if(environment.isMinecraftJavaServer() || environment.isMinecraftBedrockServer()){
            return new CloudNetServer(snapshot);
        }
        return null;
    }

    @Override
    public void sendBroadcastMessage(String channel, Document request) {
        messenger.sendMessage(NetworkIdentifier.BROADCAST, channel,request);
    }

    @Override
    public void sendProxyMessage(String channel, Document request) {
        messenger.sendMessage(NetworkIdentifier.BROADCAST_PROXY, channel,request);
    }

    @Override
    public void sendServerMessage(String channel, Document request) {
        messenger.sendMessage(NetworkIdentifier.BROADCAST_SERVER, channel,request);
    }

    @Override
    public Collection<NetworkSynchronisationCallback> getStatusCallbacks() {
        return Collections.emptyList();
    }

    @Override
    public void registerStatusCallback(Plugin<?> owner, NetworkSynchronisationCallback synchronisationCallback) {
        //Unused, always connected
    }

    @Override
    public void unregisterStatusCallback(NetworkSynchronisationCallback synchronisationCallback) {
        //Unused, always connected
    }

    @Override
    public void unregisterStatusCallbacks(Plugin<?> owner) {
        //Unused, always connected
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public String getStatus() {
        return isConnected() ? "ONLINE" : "OFFLINE";
    }

    @Override
    public int getMaxPlayerCount() {
        AbstractSyncProxyManagement service = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(AbstractSyncProxyManagement.class);
        SyncProxyMotd syncProxyMotd = service.getRandomMotd();
        if(syncProxyMotd.isAutoSlot()){
            return Math.min(
                    service.getLoginConfiguration().getMaxPlayers(),
                    getOnlineCount() + syncProxyMotd.getAutoSlotMaxPlayersDistance());
        }
        return service.getLoginConfiguration().getMaxPlayers();
    }

    @Override
    public int getOnlineCount() {
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        return playerManager.getOnlineCount();
    }

    @Override
    public Collection<OnlineMinecraftPlayer> getOnlinePlayers() {
        Collection<OnlineMinecraftPlayer> result = new ArrayList<>();
        for (ICloudPlayer onlinePlayer : BridgePlayerManager.getInstance().getOnlinePlayers()) {
            ConnectedMinecraftPlayer connected = McNative.getInstance().getLocal().getConnectedPlayer(onlinePlayer.getUniqueId());
            if(connected == null) result.add(new CloudNetOnlinePlayer(onlinePlayer));
            else result.add(connected);
        }
        return result;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(UUID uniqueId) {
        OnlineMinecraftPlayer connectedPlayer = McNative.getInstance().getLocal().getConnectedPlayer(uniqueId);
        if(connectedPlayer != null) return connectedPlayer;

        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        ICloudPlayer player = playerManager.getOnlinePlayer(uniqueId);
        return player != null ? new CloudNetOnlinePlayer(player) : null;
    }

    public OnlineMinecraftPlayer getDirectOnlinePlayer(UUID uniqueId) {
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        ICloudPlayer player = playerManager.getOnlinePlayer(uniqueId);
        return player != null ? new CloudNetOnlinePlayer(player) : null;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(String name) {
        OnlineMinecraftPlayer connectedPlayer = McNative.getInstance().getLocal().getConnectedPlayer(name);
        if(connectedPlayer != null) return connectedPlayer;

        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        ICloudPlayer player = playerManager.getFirstOnlinePlayer(name);
        return player != null ? new CloudNetOnlinePlayer(player) : null;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer(long xBoxId) {
        throw new UnsupportedOperationException("Currently not supported");
    }

    @Override
    public void broadcast(MessageComponent<?> component, VariableSet variables) {
        McNativeGlobalExecutor.broadcast(component, variables);
    }

    @Override
    public void broadcast(String permission, MessageComponent<?> component, VariableSet variables) {
        McNativeGlobalExecutor.broadcast(permission,component, variables);
    }

    @Override
    public NetworkIdentifier getIdentifier() {
        return networkIdentifier;
    }

    @Override
    public CompletableFuture<Document> sendQueryMessageAsync(String channel, Document document) {
        return messenger.sendQueryMessageAsync(NetworkIdentifier.BROADCAST,channel,document);
    }

}
