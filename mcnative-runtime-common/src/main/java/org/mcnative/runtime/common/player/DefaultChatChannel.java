/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 25.04.20, 20:51
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

package org.mcnative.runtime.common.player;

import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.player.chat.ChatChannel;
import org.mcnative.runtime.api.player.chat.ChatFormatter;
import org.mcnative.runtime.api.player.chat.GroupChatFormatter;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacket;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.api.text.format.ColoredString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class DefaultChatChannel implements ChatChannel {

    private String name;
    private Collection<OnlineMinecraftPlayer> players;
    private ChatFormatter messageFormatter;

    public DefaultChatChannel() {
        this("undefined");
    }

    public DefaultChatChannel(String name) {
        this(name,new ArrayList<>());
    }

    public DefaultChatChannel(String name, Collection<OnlineMinecraftPlayer> players) {
        this.name = name;
        this.players = players;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Collection<OnlineMinecraftPlayer> getPlayers() {
        return players;
    }

    @Override
    public void setPlayers(Collection<OnlineMinecraftPlayer> players) {
        Validate.notNull(players);
        this.players = players;
    }

    @Override
    public boolean containsPlayer(OnlineMinecraftPlayer player) {
        Validate.notNull(player);
        return players.contains(player);
    }

    @Override
    public void addPlayer(OnlineMinecraftPlayer player) {
        Validate.notNull(player);
        this.players.add(player);
    }

    @Override
    public void removePlayer(OnlineMinecraftPlayer player) {
        Validate.notNull(player);
        this.players.remove(player);
    }

    @Override
    public void sendMessage(MessageComponent<?> component, VariableSet variables) {
        for (OnlineMinecraftPlayer player : this.players) {
            player.sendMessage(component,variables);
        }
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        for (OnlineMinecraftPlayer player : this.players) {
            player.sendPacket(packet);
        }
    }

    @Override
    public void addRemovalListener(Consumer<OnlineMinecraftPlayer> listener) {
        throw new UnsupportedOperationException("Currently not supported");
    }

    @Override
    public void addAdditionListener(Consumer<OnlineMinecraftPlayer> listener) {
        throw new UnsupportedOperationException("Currently not supported");
    }

    @Override
    public Iterator<OnlineMinecraftPlayer> iterator() {
        return players.iterator();
    }

    @Override
    public void setMessageFormatter(ChatFormatter formatter) {
        Validate.notNull(formatter);
        this.messageFormatter = formatter;
    }

    @Override
    public void chat(OnlineMinecraftPlayer player, String message) {
        chat(player,message,null);
    }

    @Override
    public void chat(OnlineMinecraftPlayer sender, String message, VariableSet variables0) {
        Validate.notNull(sender,message);

        VariableSet variables = variables0 != null ? variables0: VariableSet.create();
        variables.addDescribed("player",sender);
        variables.addDescribed("sender",sender);
        variables.addDescribed("design",sender.getDesign());
        variables.add("rawMessage",message);

        if(sender.hasPermission("mcnative.chat.color")) variables.add("message",new ColoredString(message));
        else variables.add("message",message);

        sender.getDesign().appendAdditionalVariables(variables);

        if(messageFormatter == null) throw new OperationFailedException("In chat channel "+name+" is no message formatter defined");

        if(messageFormatter instanceof GroupChatFormatter){
            MessageComponent<?> output = messageFormatter.format(null,sender,variables,message);
            sendMessage(output,variables);
        }else{
            for (OnlineMinecraftPlayer player : this.players) {
                MessageComponent<?> output = messageFormatter.format(player,sender,variables,message);
                player.sendMessage(output,variables);
            }
        }
    }
}
