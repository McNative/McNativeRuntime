/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 22.02.20, 17:24
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

package org.mcnative.runtime.common.network.messaging;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.synchronisation.SynchronisationCaller;
import net.pretronic.libraries.synchronisation.SynchronisationHandler;
import net.pretronic.libraries.utility.reflect.TypeReference;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.messaging.MessageReceiver;
import org.mcnative.runtime.api.network.messaging.MessagingChannelListener;

import java.util.UUID;

public class SynchronisationMessagingAdapter implements MessagingChannelListener {

    private final static String ACTION_KEY = "SMA_ACTION";
    private final static String IDENTIFIER_KEY = "SMA_IDENTIFIER";

    private final SynchronisationHandler handler;
    private final TypeReference<?> identifierReference;

    @SuppressWarnings("unchecked")
    public SynchronisationMessagingAdapter(String channel,Class<?> identifierClass,SynchronisationHandler<?,?> handler) {
        this.handler = handler;
        this.identifierReference = new TypeReference<>(identifierClass);
        handler.init(new Caller(channel));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Document onMessageReceive(MessageReceiver receiver, UUID requestId, Document request) {
        byte action = request.getByte(ACTION_KEY);
        Object identifier = request.getObject(IDENTIFIER_KEY,identifierReference);
        if(action == 1){
            handler.onCreate(identifier,request);
        }else if(action == 2){
            handler.onUpdate(identifier,request);
        }else if(action == 3){
            handler.onDelete(identifier,request);
        }else throw new IllegalArgumentException("Received invalid synchronisation action");
        return null;
    }

    private static class Caller implements SynchronisationCaller  {

        private final String channel;

        public Caller(String channel) {
            this.channel = channel;
        }

        @Override
        public void create(Object identifier, Document document) {
            document.set(IDENTIFIER_KEY,identifier);
            document.set(ACTION_KEY,1);
            McNative.getInstance().getNetwork().sendBroadcastMessage(channel,document);
        }

        @Override
        public void createAndIgnore(Object identifier, Document document) {
            if(isConnected()) {
                create(identifier, document);
            }
        }

        @Override
        public boolean isConnected() {
            return McNative.getInstance().isNetworkAvailable() && McNative.getInstance().getNetwork().isConnected();
        }

        @Override
        public void update(Object identifier, Document document) {
            document.set(IDENTIFIER_KEY,identifier);
            document.set(ACTION_KEY,2);
            McNative.getInstance().getNetwork().sendBroadcastMessage(channel,document);
        }

        @Override
        public void updateAndIgnore(Object identifier, Document document) {
            if(isConnected()) {
                update(identifier, document);
            }
        }

        @Override
        public void delete(Object identifier, Document document) {
            document.set(IDENTIFIER_KEY,identifier);
            document.set(ACTION_KEY,3);
            McNative.getInstance().getNetwork().sendBroadcastMessage(channel,document);
        }

        @Override
        public void deleteAndIgnore(Object identifier, Document document) {
            if(isConnected()) {
                delete(identifier, document);
            }
        }
    }
}
