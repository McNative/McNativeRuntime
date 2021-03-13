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

package org.mcnative.runtime.protocol.java.codec.bossbar;

import io.netty.buffer.ByteBuf;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftBossBarPacket;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class MinecraftBossBarPacketCodecV1_9 implements MinecraftPacketCodec<MinecraftBossBarPacket> {

    @Override
    public void read(MinecraftBossBarPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {

    }

    @Override
    public void write(MinecraftBossBarPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        MinecraftProtocolUtil.writeUUID(buffer,packet.getBarId());
        MinecraftProtocolUtil.writeVarInt(buffer,packet.getAction().ordinal());
        if(packet.getAction() == MinecraftBossBarPacket.Action.ADD){
            MinecraftProtocolUtil.writeString(buffer,packet.getTitle().compileToString(connection.getProtocolVersion(),packet.getTitleVariables() != null ? packet.getTitleVariables() : VariableSet.createEmpty()));
            buffer.writeFloat(packet.getHealth());
            MinecraftProtocolUtil.writeVarInt(buffer,packet.getColor().ordinal());
            MinecraftProtocolUtil.writeVarInt(buffer,packet.getDivider().ordinal());
            buffer.writeByte((byte)packet.getFlag().ordinal());
        }else if(packet.getAction() == MinecraftBossBarPacket.Action.UPDATE_HEALTH){
            buffer.writeFloat(packet.getHealth());
        }else if(packet.getAction() == MinecraftBossBarPacket.Action.UPDATE_TITLE){
            MinecraftProtocolUtil.writeString(buffer,packet.getTitle().compileToString(connection.getProtocolVersion(),packet.getTitleVariables() != null ? packet.getTitleVariables() : VariableSet.createEmpty()));
        }else if(packet.getAction() == MinecraftBossBarPacket.Action.UPDATE_STYLE){
            MinecraftProtocolUtil.writeVarInt(buffer,packet.getColor().ordinal());
            MinecraftProtocolUtil.writeVarInt(buffer,packet.getDivider().ordinal());
        }else if(packet.getAction() == MinecraftBossBarPacket.Action.UPDATE_FLAGS){
            buffer.writeByte((byte)packet.getFlag().ordinal());
        }
    }
}
