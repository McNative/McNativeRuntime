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

package org.mcnative.runtime.protocol.java.codec.title;

import io.netty.buffer.ByteBuf;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftTitlePacket;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;
import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.map;

public class MinecraftTitlePacketCodecV1_17 implements MinecraftPacketCodec<MinecraftTitlePacket> {

    @Override
    public void read(MinecraftTitlePacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        //Currently not supported
    }

    @Override
    public void write(MinecraftTitlePacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        buffer.resetWriterIndex();
        buffer.resetReaderIndex();
        if(packet.getAction() == MinecraftTitlePacket.Action.SET_TITLE){
            int protocolNumber;
            if(connection.getProtocolVersion().isNewerOrSame(MinecraftProtocolVersion.JE_1_18_2)) protocolNumber = 0x5A;
            else protocolNumber = 0x59;
            MinecraftProtocolUtil.writeVarInt(buffer,protocolNumber);
            MinecraftProtocolUtil.writeString(buffer,((MessageComponent<?>)packet.getRawData())
                    .compileToString(connection.getProtocolVersion(),packet.getVariables()!=null?packet.getVariables(): VariableSet.createEmpty()));
        }else if(packet.getAction() == MinecraftTitlePacket.Action.SET_SUBTITLE){
            int protocolNumber;
            if(connection.getProtocolVersion().isNewerOrSame(MinecraftProtocolVersion.JE_1_18_2)) protocolNumber = 0x58;
            else protocolNumber = 0x57;
            MinecraftProtocolUtil.writeVarInt(buffer,protocolNumber);
            MinecraftProtocolUtil.writeString(buffer,((MessageComponent<?>)packet.getRawData())
                    .compileToString(connection.getProtocolVersion(),packet.getVariables()!=null?packet.getVariables(): VariableSet.createEmpty()));
        }else if(packet.getAction() == MinecraftTitlePacket.Action.SET_TIME){
            int protocolNumber;
            if(connection.getProtocolVersion().isNewerOrSame(MinecraftProtocolVersion.JE_1_18_2)) protocolNumber = 0x5B;
            else protocolNumber = 0x5A;
            MinecraftProtocolUtil.writeVarInt(buffer,protocolNumber);
            int[] array = (int[]) packet.getRawData();
            buffer.writeInt(array[0]);
            buffer.writeInt(array[1]);
            buffer.writeInt(array[2]);
        }else if(packet.getAction() == MinecraftTitlePacket.Action.HIDE || packet.getAction() == MinecraftTitlePacket.Action.RESET){
            MinecraftProtocolUtil.writeVarInt(buffer,0x10);
            buffer.writeBoolean(packet.getAction() == MinecraftTitlePacket.Action.RESET);
        }
    }
}
