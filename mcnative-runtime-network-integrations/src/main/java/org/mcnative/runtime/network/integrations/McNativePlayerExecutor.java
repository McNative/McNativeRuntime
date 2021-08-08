/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.07.20, 11:23
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

package org.mcnative.runtime.network.integrations;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.ServerConnectReason;
import org.mcnative.runtime.api.network.component.server.ServerConnectResult;
import org.mcnative.runtime.api.player.Title;
import org.mcnative.runtime.api.player.chat.ChatPosition;
import org.mcnative.runtime.api.player.sound.SoundCategory;
import org.mcnative.runtime.api.protocol.MinecraftEdition;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacket;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class McNativePlayerExecutor {

    public static int getPing(UUID uniqueId) {
        try {
            return getPingAsync(uniqueId).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new OperationFailedException(e);
        }
    }

    public static CompletableFuture<Integer> getPingAsync(UUID uniqueId) {
        CompletableFuture<Integer> resultFuture = new CompletableFuture<>();
        executePlayerBasedFuture(uniqueId,Document.newDocument()
                .set("action","getPing"))
                .thenAccept(documentEntries -> resultFuture.complete(documentEntries.getInt("ping")));
        return resultFuture;
    }

    public static void connect(UUID uniqueId, MinecraftServer target, ServerConnectReason reason) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","connect")
                .set("target",target.getName())
                .set("reason",reason));
    }

    public static CompletableFuture<ServerConnectResult> connectAsync(UUID uniqueId, MinecraftServer target, ServerConnectReason reason) {
        CompletableFuture<ServerConnectResult> resultFuture = new CompletableFuture<>();
        executePlayerBasedFuture(uniqueId,Document.newDocument()
                .set("action","connectAsync")
                .set("target",target.getName())
                .set("reason",reason))
                .thenAccept(documentEntries -> resultFuture.complete(documentEntries.getObject("result",ServerConnectResult.class)));
        return resultFuture;
    }

    public static void kick(UUID uniqueId, MessageComponent<?> message, VariableSet variables) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","kick")
                .set("message",message.compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),variables)));
    }

    public static void kickLocal(UUID uniqueId, MessageComponent<?> message, VariableSet variables) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","kickLocal")
                .set("message",message.compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),variables)));
    }

    public static void performCommand(UUID uniqueId,String command) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","performCommand")
                .set("command",command));
    }

    public static void chat(UUID uniqueId,String message) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","chat")
                .set("message",message));
    }

    public static void sendMessage(UUID uniqueId, ChatPosition position, MessageComponent<?> component, VariableSet variables) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","sendMessage")
                .set("position",position.getId())
                .set("text",component.compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),variables)));
    }

    public static void sendActionbar(UUID uniqueId,MessageComponent<?> message, VariableSet variables, long staySeconds) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","sendActionbar")
                .set("staySeconds",staySeconds)
                .set("text",message.compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),variables)));
    }

    public static void sendTitle(UUID uniqueId, Title title) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","sendTitle")
                .set("timing",title.getTiming())
                .set("title",title.getTitle() != null ? title.getTitle().compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),title.getVariables()) : null)
                .set("subTitle",title.getTitle() != null ? title.getSubTitle().compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),title.getVariables()) : null));
    }

    public static void resetTitle(UUID uniqueId) {
        executePlayerBased(uniqueId,Document.newDocument().set("action","resetTitle"));
    }

    public static void playSound(UUID uniqueId, String sound, SoundCategory category, float volume, float pitch) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","playSound")
                .set("sound",sound)
                .set("category",category)
                .set("volume",volume)
                .set("pitch",pitch));
    }

    public static void stopSound(UUID uniqueId, String sound, SoundCategory category) {
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","stopSound")
                .set("sound",sound)
                .set("category",category));
    }

    public static void sendPacket(UUID uniqueId, MinecraftPacket packet) {//@Todo optimize with byte serialization
        executePlayerBased(uniqueId,Document.newDocument()
                .set("action","sendPacket")
                .set("packetClass",packet.getClass())
                .set("packetData",Document.newDocument(packet)));
    }

    private static void executePlayerBased(UUID uniqueId, Document data){
        data.set("uniqueId",uniqueId);
        McNative.getInstance().getNetwork().getMessenger()
                .sendMessage(NetworkIdentifier.BROADCAST_PROXY,"mcnative_player",data);
    }

    private static CompletableFuture<Document> executePlayerBasedFuture(UUID uniqueId, Document data){
        data.set("uniqueId",uniqueId);
        return McNative.getInstance().getNetwork().getMessenger()
                .sendQueryMessageAsync(NetworkIdentifier.BROADCAST_PROXY,"mcnative_player",data);
    }
}
