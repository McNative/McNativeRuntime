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

package org.mcnative.runtime.network.integrations.cloudnet.v2;

import de.dytanic.cloudnet.api.CloudAPI;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import org.mcnative.runtime.common.network.messaging.AbstractMessenger;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.ProxyServer;
import org.mcnative.runtime.api.network.messaging.MessageReceiver;
import org.mcnative.runtime.api.network.messaging.MessagingChannelListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class CloudNetV2Messenger extends AbstractMessenger {

    private final String CHANNEL_NAME = "mcnative";

    private final String MESSAGE_NAME_REQUEST = "request";
    private final String MESSAGE_NAME_RESPONSE = "response";

    private final Executor executor;
    private final Map<UUID, CompletableFuture<Document>> resultListeners;

    public CloudNetV2Messenger(Executor executor) {
        this.executor = executor;
        this.resultListeners = new ConcurrentHashMap<>();
    }

    @Override
    public String getTechnology() {
        return "CloudNet V2";
    }

    @Override
    public boolean isAvailable() {
        return CloudAPI.getInstance().getNetworkConnection().isConnected();
    }

    @Override
    public void sendMessage(NetworkIdentifier receiver, String channel, Document request, UUID requestId) {
        if(receiver.equals(NetworkIdentifier.BROADCAST)){
            de.dytanic.cloudnet.lib.utility.document.Document requestData = createRequestData(channel,request, requestId);
            CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST,requestData);
            CloudAPI.getInstance().sendCustomSubServerMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST,requestData);
        }else if(receiver.equals(NetworkIdentifier.BROADCAST_SERVER)){
            de.dytanic.cloudnet.lib.utility.document.Document requestData = createRequestData(channel,request, requestId);
            CloudAPI.getInstance().sendCustomSubServerMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST,requestData);
        }else if(receiver.equals(NetworkIdentifier.BROADCAST_PROXY)){
            de.dytanic.cloudnet.lib.utility.document.Document requestData = createRequestData(channel,request, requestId);
            CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST,requestData);
        }else throw new UnsupportedOperationException("Network identifier is not supported");
    }

    @Override
    public void sendMessage(MessageReceiver receiver, String channel, Document request, UUID requestId) {
        if(receiver instanceof ProxyServer){
            CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST
                    ,createRequestData(channel,request, requestId)
                    ,receiver.getIdentifier().getName());
        }else if(receiver instanceof MinecraftServer){
            CloudAPI.getInstance().sendCustomSubServerMessage(CHANNEL_NAME,MESSAGE_NAME_REQUEST
                    ,createRequestData(channel,request, requestId)
                    ,receiver.getIdentifier().getName());
        }else throw new UnsupportedOperationException("Message receiver is not a proxy or server");
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

    public void handleMessageEvent(String channel0,String message, de.dytanic.cloudnet.lib.utility.document.Document document){
        if(channel0.equals(CHANNEL_NAME)){
            if(message.equals(MESSAGE_NAME_REQUEST)){

                String channel = document.getString("channel");
                boolean proxy = document.getBoolean("proxy");
                String sender = document.getString("sender");
                UUID identifier = UUID.fromString(document.getString("identifier"));
                Document data = DocumentFileType.JSON.getReader().read(document.getString("data"));

                MessagingChannelListener listener = getChannelListener(channel);
                if(listener != null){
                    Document result = listener.onMessageReceive(null,identifier,data);
                    if(result != null){
                        if(proxy){
                            CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME
                                    ,MESSAGE_NAME_RESPONSE
                                    ,createResponseData(identifier,result)
                                    ,sender);
                        }else{
                            CloudAPI.getInstance().sendCustomSubServerMessage(CHANNEL_NAME
                                    ,MESSAGE_NAME_RESPONSE
                                    ,createResponseData(identifier,result)
                                    ,sender);
                        }
                    }
                }
            }else if(message.equals(MESSAGE_NAME_RESPONSE)){
                UUID identifier = UUID.fromString(document.getString("identifier"));
                CompletableFuture<Document> listener = resultListeners.remove(identifier);
                if(listener != null){
                    Document data = DocumentFileType.JSON.getReader().read(document.getString("data"));
                    listener.complete(data);
                }
            }
        }
    }

    private de.dytanic.cloudnet.lib.utility.document.Document createRequestData(String channel,Document request, UUID requestId){
        de.dytanic.cloudnet.lib.utility.document.Document result = new de.dytanic.cloudnet.lib.utility.document.Document();
        result.append("sender",CloudAPI.getInstance().getServerId());
        result.append("proxy", McNative.getInstance().getPlatform().isProxy());
        result.append("channel",channel);
        result.append("identifier",requestId.toString());
        result.append("data", DocumentFileType.JSON.getWriter().write(request,false));
        return result;
    }

    private de.dytanic.cloudnet.lib.utility.document.Document createResponseData(UUID requestId,Document response){
        de.dytanic.cloudnet.lib.utility.document.Document result = new de.dytanic.cloudnet.lib.utility.document.Document();
        result.append("identifier",requestId.toString());
        result.append("data", DocumentFileType.JSON.getWriter().write(response,false));
        return result;
    }
}
