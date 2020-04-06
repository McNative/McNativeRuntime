/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.01.20, 18:26
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

package org.mcnative.bungeecord.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import org.mcnative.bungeecord.McNativeLauncher;
import org.mcnative.bungeecord.server.BungeeCordServerMap;
import org.mcnative.common.McNative;
import org.mcnative.common.network.NetworkIdentifier;
import org.mcnative.common.network.component.server.MinecraftServer;
import org.mcnative.common.network.messaging.AbstractMessenger;
import org.mcnative.common.network.messaging.MessageReceiver;
import org.mcnative.common.network.messaging.MessagingChannelListener;
import org.mcnative.common.protocol.MinecraftProtocolUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class PluginMessageMessenger extends AbstractMessenger implements Listener {

    private final String CHANNEL_NAME_REQUEST = "mcnative:request";

    private final String CHANNEL_NAME_RESPONSE = "mcnative:response";

    private final Executor executor;
    private final BungeeCordServerMap serverMap;
    private final Map<UUID,CompletableFuture<Document>> resultListeners;

    public PluginMessageMessenger(Executor executor, BungeeCordServerMap serverMap) {
        super();
        this.executor = executor;
        this.serverMap = serverMap;

        this.resultListeners = new ConcurrentHashMap<>();

        ProxyServer.getInstance().registerChannel(CHANNEL_NAME_REQUEST);
        ProxyServer.getInstance().registerChannel(CHANNEL_NAME_RESPONSE);
        ProxyServer.getInstance().getPluginManager().registerListener(McNativeLauncher.getPlugin(),this);
    }

    @Override
    public String getTechnology() {
        return "Minecraft Plugin Message Channels";
    }

    @Override
    public boolean isAvailable() {
        return true;//Always available
    }

    @Override
    public void sendMessage(NetworkIdentifier receiver, String channel, Document request, UUID requestId) {
        if(receiver.equals(NetworkIdentifier.BROADCAST) || receiver.equals(NetworkIdentifier.BROADCAST_SERVER)){
            UUID sender = McNative.getInstance().getNetwork().getLocalIdentifier().getUniqueId();
            byte[] data = writeData(sender,requestId,channel,request);
            for (MinecraftServer server : McNative.getInstance().getNetwork().getServers()) {
                if(server.getOnlineCount() > 0){
                    server.sendData(CHANNEL_NAME_REQUEST,data);
                }
            }
        }else throw new UnsupportedOperationException("Network identifier is not supported");
    }

    @Override
    public void sendMessage(MessageReceiver receiver, String channel, Document request,UUID requestId) {
        if(receiver instanceof MinecraftServer){
            UUID sender = McNative.getInstance().getNetwork().getLocalIdentifier().getUniqueId();
            byte[] data = writeData(sender,requestId,channel,request);
            ((MinecraftServer) receiver).sendData(CHANNEL_NAME_REQUEST,data);
        }else throw new UnsupportedOperationException("Plugin messaging requires a minecraft server as receiver");
    }

    @Override
    public Document sendQueryMessage(MessageReceiver receiver, String channel, Document request) {
        try {
            return sendQueryMessageAsync(receiver,channel,request).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public CompletableFuture<Document> sendQueryMessageAsync(MessageReceiver receiver, String channel, Document request) {
        CompletableFuture<Document> result = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        this.resultListeners.put(id,result);
        executor.execute(()-> sendMessage(receiver, channel, request,id));
        return result;
    }

    private byte[] writeData(UUID sender,UUID requestId,String channel, Document request) {
        ByteBuf buffer = Unpooled.directBuffer();
        MinecraftProtocolUtil.writeUUID(buffer,sender);//Sender
        MinecraftProtocolUtil.writeUUID(buffer,requestId);//RequestId
        MinecraftProtocolUtil.writeString(buffer,channel);//Channel
        DocumentFileType.BINARY.getWriter().write(new ByteBufOutputStream(buffer),StandardCharsets.UTF_8,request,false);//Request
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        buffer.release();
        return data;
    }

    private byte[] writeResponse(UUID identifier,Document response) {
        ByteBuf buffer = Unpooled.directBuffer();
        MinecraftProtocolUtil.writeUUID(buffer,identifier);//Sender
        DocumentFileType.BINARY.getWriter().write(new ByteBufOutputStream(buffer),StandardCharsets.UTF_8,response,false);//Response
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        buffer.release();
        return data;
    }

    private byte[] writeForward(ByteBuf buffer,UUID sender){
        buffer.markWriterIndex();
        buffer.setIndex(0,0);
        MinecraftProtocolUtil.writeUUID(buffer,sender);
        buffer.resetWriterIndex();

        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        return data;
    }

    @EventHandler
    public void handleRequest(PluginMessageEvent event){
        if(event.getSender() instanceof Server){
            if(event.getTag().equals(CHANNEL_NAME_REQUEST)){
                MinecraftServer sender = serverMap.getMappedServer(((Server) event.getSender()).getInfo());
                ByteBuf buffer = Unpooled.copiedBuffer(event.getData());
                handleDataRequest(sender,buffer);
                buffer.release();
            }else if(event.getTag().equals(CHANNEL_NAME_RESPONSE)){
                ByteBuf buffer = Unpooled.copiedBuffer(event.getData());
                handleDataResponse(buffer);
                buffer.release();
            }
        }
    }

    private void handleDataRequest(MinecraftServer sender,ByteBuf buffer){
        UUID destinationId = MinecraftProtocolUtil.readUUID(buffer);
        UUID identifier = MinecraftProtocolUtil.readUUID(buffer);

        boolean broadcast = NetworkIdentifier.BROADCAST.getUniqueId().equals(destinationId) || NetworkIdentifier.BROADCAST_PROXY.getUniqueId().equals(destinationId);
        boolean local = McNative.getInstance().getNetwork().getLocalIdentifier().getUniqueId().equals(destinationId);

        System.out.println("Received BC: "+broadcast);

        if(broadcast || local){
            String channel = MinecraftProtocolUtil.readString(buffer);
            MessagingChannelListener listener = getChannelListener(channel);
            if(listener != null){
                Document data = DocumentFileType.BINARY.getReader().read(new ByteBufInputStream(buffer));
                Document result = listener.onMessageReceive(sender,identifier,data);
                if(!broadcast && result != null){
                    byte[] response = writeResponse(identifier,result);
                    sender.sendData(CHANNEL_NAME_RESPONSE,response);///write result
                    return;
                }
            }

            if(broadcast){
                byte[] forward = writeForward(buffer,sender.getIdentifier().getUniqueId());
                for (MinecraftServer server : McNative.getInstance().getNetwork().getServers()) {
                    if(!server.equals(sender)){
                        System.out.println("Forwarding data to "+sender.getName());
                        server.sendData(CHANNEL_NAME_REQUEST,forward);
                    }
                }
            }
        }else{
            byte[] forward = writeForward(buffer,sender.getIdentifier().getUniqueId());
            MinecraftServer server = McNative.getInstance().getNetwork().getServer(destinationId);
            if(server != null){
                server.sendData(CHANNEL_NAME_REQUEST,forward);
            }
        }
    }

    private void handleDataResponse(ByteBuf buffer){
        UUID destinationId = MinecraftProtocolUtil.readUUID(buffer);
        boolean local = McNative.getInstance().getNetwork().getLocalIdentifier().getUniqueId().equals(destinationId);
        if(local){
            UUID identifier = MinecraftProtocolUtil.readUUID(buffer);
            CompletableFuture<Document> listener = resultListeners.remove(identifier);
            if(listener != null){
                Document data = DocumentFileType.BINARY.getReader().read(new ByteBufInputStream(buffer));
                listener.complete(data);
            }
        }else{
            MinecraftServer server = McNative.getInstance().getNetwork().getServer(destinationId);
            if(server != null){
                byte[] data = new byte[buffer.readableBytes()];
                buffer.readBytes(data);
            }
        }
    }

}
