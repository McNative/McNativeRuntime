/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 26.06.20, 21:40
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

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.common.player.OfflineMinecraftPlayer;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.ProxyServer;
import org.mcnative.runtime.api.network.component.server.ServerConnectReason;
import org.mcnative.runtime.api.network.component.server.ServerConnectResult;
import org.mcnative.runtime.api.player.DeviceInfo;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.player.Title;
import org.mcnative.runtime.api.player.chat.ChatPosition;
import org.mcnative.runtime.api.player.sound.Instrument;
import org.mcnative.runtime.api.player.sound.Note;
import org.mcnative.runtime.api.player.sound.Sound;
import org.mcnative.runtime.api.player.sound.SoundCategory;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacket;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.network.integrations.McNativePlayerExecutor;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNetOnlinePlayer extends OfflineMinecraftPlayer implements OnlineMinecraftPlayer {

    private final CloudPlayer player;

    public CloudNetOnlinePlayer(CloudPlayer player) {
        super(null);
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public long getFirstPlayed() {
        return player.getFirstLogin();
    }

    @Override
    public long getLastPlayed() {
        return player.getLastLogin();
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(player.getPlayerConnection().getHost(),player.getPlayerConnection().getPort());
    }

    @Override
    public DeviceInfo getDevice() {
        return DeviceInfo.JAVA;
    }

    @Override
    public boolean isOnlineMode() {
        return player.getPlayerConnection().isOnlineMode();
    }

    @Override
    public int getPing() {
        return McNativePlayerExecutor.getPing(player.getUniqueId());
    }

    @Override
    public CompletableFuture<Integer> getPingAsync() {
        return McNativePlayerExecutor.getPingAsync(player.getUniqueId());
    }

    @Override
    public ProxyServer getProxy() {
        return McNative.getInstance().getNetwork().getProxy(player.getProxy());
    }

    @Override
    public MinecraftServer getServer() {
        return McNative.getInstance().getNetwork().getServer(player.getServer());
    }

    @Override
    public void connect(MinecraftServer target, ServerConnectReason reason) {
        McNativePlayerExecutor.connect(player.getUniqueId(),target,reason);
    }

    @Override
    public CompletableFuture<ServerConnectResult> connectAsync(MinecraftServer target, ServerConnectReason reason) {
        return McNativePlayerExecutor.connectAsync(player.getUniqueId(),target,reason);
    }

    @Override
    public void kick(MessageComponent<?> message, VariableSet variables) {
        McNativePlayerExecutor.kick(player.getUniqueId(),message,variables);
    }

    @Override
    public void performCommand(String command) {
        McNativePlayerExecutor.performCommand(player.getUniqueId(),command);
    }

    @Override
    public void chat(String message) {
        McNativePlayerExecutor.chat(player.getUniqueId(),message);
    }

    @Override
    public void sendMessage(ChatPosition position, MessageComponent<?> component, VariableSet variables) {
        McNativePlayerExecutor.sendMessage(player.getUniqueId(),position,component,variables);
    }

    @Override
    public void sendActionbar(MessageComponent<?> message, VariableSet variables) {
        sendActionbar(message,variables,-1);
    }

    @Override
    public void sendActionbar(MessageComponent<?> message, VariableSet variables, long staySeconds) {
        McNativePlayerExecutor.sendActionbar(player.getUniqueId(),message,variables,staySeconds);
    }

    @Override
    public void sendTitle(Title title) {
        McNativePlayerExecutor.sendTitle(player.getUniqueId(),title);
    }

    @Override
    public void resetTitle() {
        McNativePlayerExecutor.resetTitle(player.getUniqueId());
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void playNote(Instrument instrument, Note note) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void playSound(Sound sound, SoundCategory category, float volume, float pitch) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopSound(Sound sound) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopSound(String sound, SoundCategory category) {
        throw new UnsupportedOperationException();
    }
}
