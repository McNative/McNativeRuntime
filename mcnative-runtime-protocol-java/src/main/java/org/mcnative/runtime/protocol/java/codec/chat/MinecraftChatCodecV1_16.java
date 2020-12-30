/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.09.19, 18:15
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

package org.mcnative.runtime.protocol.java.codec.chat;

import io.netty.buffer.ByteBuf;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import org.mcnative.actionframework.sdk.common.protocol.codec.BufferUtil;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.player.chat.ChatPosition;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.util.UUID;

public class MinecraftChatCodecV1_16 implements MinecraftPacketCodec<MinecraftChatPacket> {

    private final static UUID EMPTY = new UUID(0,0);

    /*
    public final static PacketIdentifier IDENTIFIER = newIdentifier(MinecraftChatCodecV1_7.class
            ,on(PacketDirection.OUTGOING
                    ,map(MinecraftProtocolVersion.JE_1_7,0x02)
                    ,map(MinecraftProtocolVersion.JE_1_9,0x0F)
                    ,map(MinecraftProtocolVersion.JE_1_13,0x0E)
                    ,map(MinecraftProtocolVersion.JE_1_15,0x0F)
                    ,map(MinecraftProtocolVersion.JE_1_16,0x0E))
            ,on(PacketDirection.INCOMING
                    ,map(MinecraftProtocolVersion.JE_1_7,0x01)
                    ,map(MinecraftProtocolVersion.JE_1_9,0x02)
                    ,map(MinecraftProtocolVersion.JE_1_12,0x03)
                    ,map(MinecraftProtocolVersion.JE_1_12_1,0x02)
                    ,map(MinecraftProtocolVersion.JE_1_14,0x03)));
     */

    @Override
    public void read(MinecraftChatPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            packet.setMessage(Text.decompile(MinecraftProtocolUtil.readString(buffer)));
            packet.setPosition(ChatPosition.of(buffer.readByte()));
            packet.setSender(BufferUtil.readUniqueId(buffer));
        }else if(direction == PacketDirection.INCOMING){
            packet.setMessage(Text.of(MinecraftProtocolUtil.readString(buffer)));
        }
    }

    @Override
    public void write(MinecraftChatPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            Language language = null;
            if(connection instanceof LanguageAble) language = ((LanguageAble) connection).getLanguage();
            VariableSet variables = packet.getVariables()!=null?packet.getVariables():VariableSet.createEmpty();
            UUID sender = packet.getSender() != null ? packet.getSender() : EMPTY;

            MinecraftProtocolUtil.writeString(buffer,packet.getMessage().compileToString(connection,variables,language));
            buffer.writeByte(packet.getPosition().getId());
            BufferUtil.writeUniqueId(buffer,sender);
        }else if(direction == PacketDirection.INCOMING){
            MinecraftProtocolUtil.writeString(buffer,packet.getMessage().toPlainText());
        }
    }
}
