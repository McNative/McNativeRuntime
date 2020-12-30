/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.04.20, 18:10
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

package org.mcnative.runtime.common.network.messaging;

import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.synchronisation.SynchronisationHandler;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.network.messaging.MessagingChannelListener;
import org.mcnative.runtime.api.network.messaging.Messenger;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractMessenger implements Messenger {

    private final Collection<MessageEntry> messageListeners;

    public AbstractMessenger() {
        this.messageListeners = new ArrayList<>();
    }

    /* Channel registration */

    @Override
    public Collection<String> getChannels() {
        return Iterators.map(this.messageListeners, entry -> entry.name);
    }

    @Override
    public Collection<String> getChannels(Plugin<?> owner) {
        Validate.notNull(owner);
        return Iterators.map(this.messageListeners
                , entry -> entry.name
                , entry -> entry.owner.equals(owner));
    }

    @Override
    public MessagingChannelListener getChannelListener(String name) {
        Validate.notNull(name);
        MessageEntry result = Iterators.findOne(this.messageListeners, entry -> entry.name.equalsIgnoreCase(name));
        return result != null ? result.listener : null;
    }

    @Override
    public void registerChannel(String channel, ObjectOwner owner, MessagingChannelListener listener) {
        Validate.notNull(channel,listener,owner);
        if(getChannelListener(channel) != null) throw new IllegalArgumentException("Message channel "+channel+" already in use");
        this.messageListeners.add(new MessageEntry(channel,owner,listener));
    }

    @Override
    public <I> void registerSynchronizingChannel(String channel, Plugin<?> owner, Class<I> identifier, SynchronisationHandler<?, I> handler) {
        registerChannel(channel,owner,new SynchronisationMessagingAdapter(channel,identifier,handler));
    }

    @Override
    public void unregisterChannel(String channel) {
        Validate.notNull(channel);
        Iterators.removeOne(this.messageListeners, entry -> entry.name.equalsIgnoreCase(channel));
    }

    @Override
    public void unregisterChannel(MessagingChannelListener listener) {
        Validate.notNull(listener);
        Iterators.removeSilent(this.messageListeners, entry -> entry.listener.equals(listener));
    }

    @Override
    public void unregisterChannels(Plugin<?> owner) {
        Validate.notNull(owner);
        Iterators.removeSilent(this.messageListeners, entry -> entry.owner.equals(owner));
    }

    private static class MessageEntry {

        private final String name;
        private final ObjectOwner owner;
        private final MessagingChannelListener listener;

        public MessageEntry(String name, ObjectOwner owner, MessagingChannelListener listener) {
            this.name = name;
            this.owner = owner;
            this.listener = listener;
        }
    }
}
