/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.07.20, 10:35
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
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkOperations;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.ProxyServer;
import org.mcnative.runtime.api.network.component.server.ServerConnectReason;
import org.mcnative.runtime.api.network.component.server.ServerConnectResult;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNetV3NetworkOperations implements NetworkOperations {
 
    private final CloudNetV3Network network;

    public CloudNetV3NetworkOperations(CloudNetV3Network network) {
        this.network = network;
    }

    @Override
    public ProxyServer getProxy(OnlineMinecraftPlayer player0) {
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        ICloudPlayer player = playerManager.getOnlinePlayer(player0.getUniqueId());
        if(player == null) return null;
        return McNative.getInstance().getNetwork().getProxy(player.getLoginService().getUniqueId());
    }

    @Override
    public MinecraftServer getServer(OnlineMinecraftPlayer player0) {
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        ICloudPlayer player = playerManager.getFirstOnlinePlayer(player0.getName());
        if(player == null) return null;
        UUID uniqueId = player.getConnectedService().getUniqueId();
        return McNative.getInstance().getNetwork().getServer(uniqueId);
    }

    @Override
    public void connect(OnlineMinecraftPlayer player, MinecraftServer target, ServerConnectReason reason) {
        network.getOnlinePlayer(player.getUniqueId()).connect(target, reason);
    }

    @Override
    public CompletableFuture<ServerConnectResult> connectAsync(OnlineMinecraftPlayer player, MinecraftServer target, ServerConnectReason reason) {
        return network.getDirectOnlinePlayer(player.getUniqueId()).connectAsync(target,reason);
    }

    @Override
    public void kick(OnlineMinecraftPlayer player, MessageComponent<?> message, VariableSet variables) {
        network.getDirectOnlinePlayer(player.getUniqueId()).kick(message, variables);
    }
}
