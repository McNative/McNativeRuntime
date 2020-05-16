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

package org.mcnative.bungeecord.network.cloudnet.v3;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.bungee.event.BungeeChannelMessageReceiveEvent;
import de.dytanic.cloudnet.wrapper.Wrapper;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import org.mcnative.common.McNative;
import org.mcnative.common.network.NetworkIdentifier;
import org.mcnative.common.network.messaging.AbstractMessenger;
import org.mcnative.common.network.messaging.MessageReceiver;
import org.mcnative.common.network.messaging.MessagingChannelListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class CloudNetV3Messenger extends AbstractMessenger implements Listener {

    private final String CHANNEL_NAME = "mcnative";

    private final String MESSAGE_NAME_REQUEST = "request";
    private final String MESSAGE_NAME_RESPONSE = "response";

    private final Executor executor;
    private final Map<UUID, CompletableFuture<Document>> resultListeners;

    public CloudNetV3Messenger(Executor executor) {
        this.executor = executor;
        this.resultListeners = new ConcurrentHashMap<>();
    }

    @Override
    public String getTechnology() {
        return "CloudNet V2";
    }

    @Override
    public boolean isAvailable() {
        return true;//No method for checking network connection found
    }

    @Override
    public void sendMessage(NetworkIdentifier receiver, String channel, Document request, UUID requestId) {
        if(receiver.equals(NetworkIdentifier.BROADCAST)){
            JsonDocument requestData = createRequestData(channel,request, requestId);
            Wrapper.getInstance().getMessenger().sendChannelMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST,requestData);
        }else if(receiver.equals(NetworkIdentifier.BROADCAST_SERVER)){
            JsonDocument requestData = createRequestData(channel,request, requestId);
            requestData.append("type","SERVER");
            Wrapper.getInstance().getMessenger().sendChannelMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST,requestData);
        }else if(receiver.equals(NetworkIdentifier.BROADCAST_PROXY)){
            JsonDocument requestData = createRequestData(channel,request, requestId);
            requestData.append("type","PROXY");
            Wrapper.getInstance().getMessenger().sendChannelMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST,requestData);
        }else throw new UnsupportedOperationException("Network identifier is not supported");
    }

    @Override
    public void sendMessage(MessageReceiver receiver, String channel, Document request, UUID requestId) {
        ServiceInfoSnapshot service = Wrapper.getInstance().getCloudServiceProvider().getCloudService(receiver.getIdentifier().getUniqueId());
        if(service != null){
            JsonDocument requestData = createRequestData(channel,request, requestId);
            Wrapper.getInstance().getMessenger().sendChannelMessage(service,CHANNEL_NAME, MESSAGE_NAME_RESPONSE,requestData);
        }
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

    @Override
    public CompletableFuture<Document> sendQueryMessageAsync(NetworkIdentifier receiver, String channel, Document request) {
        CompletableFuture<Document> result = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        this.resultListeners.put(id,result);
        executor.execute(()-> sendMessage(receiver, channel, request,id));
        return result;
    }

    @EventHandler
    public void onMessageReceive(BungeeChannelMessageReceiveEvent event){
        if(event.getChannel().equals(CHANNEL_NAME)){
            if(event.getMessage().equals(MESSAGE_NAME_REQUEST)){
                String channel = event.getData().getString("channel");
                UUID sender = UUID.fromString(event.getData().getString("sender"));
                UUID identifier = UUID.fromString(event.getData().getString("identifier"));
                Document data = DocumentFileType.JSON.getReader().read(event.getData().getString("data"));

                String type = event.getData().getString("type");
                if(type != null){
                    if(type.equals("PROXY") && !McNative.getInstance().getPlatform().isProxy()) return;
                    else if(type.equals("SERVER") && !McNative.getInstance().getPlatform().isService()) return;
                }

                MessagingChannelListener listener = getChannelListener(channel);
                if(listener != null){
                    Document result = listener.onMessageReceive(null,identifier,data);
                    if(result != null){
                        ServiceInfoSnapshot service = Wrapper.getInstance().getCloudServiceProvider().getCloudService(sender);
                        Wrapper.getInstance().getMessenger().sendChannelMessage(service,CHANNEL_NAME, MESSAGE_NAME_RESPONSE
                                ,createResponseData(identifier,result));
                    }
                }
            }else if(event.getMessage().equals(MESSAGE_NAME_RESPONSE)){
                UUID identifier = UUID.fromString(event.getData().getString("identifier"));
                CompletableFuture<Document> listener = resultListeners.remove(identifier);
                if(listener != null){
                    Document data = DocumentFileType.JSON.getReader().read(event.getData().getString("data"));
                    listener.complete(data);
                }
            }
        }
    }

    private JsonDocument createRequestData(String channel,Document request, UUID requestId){
        JsonDocument result = new JsonDocument();
        result.append("sender",Wrapper.getInstance().getServiceId().getUniqueId().toString());
        result.append("channel",channel);
        result.append("identifier",requestId.toString());
        result.append("data", DocumentFileType.JSON.getWriter().write(request,false));
        return result;
    }

    private JsonDocument createResponseData(UUID requestId,Document response){
        JsonDocument result = new JsonDocument();
        result.append("identifier",requestId.toString());
        result.append("data", DocumentFileType.JSON.getWriter().write(response,false));
        return result;
    }

}
