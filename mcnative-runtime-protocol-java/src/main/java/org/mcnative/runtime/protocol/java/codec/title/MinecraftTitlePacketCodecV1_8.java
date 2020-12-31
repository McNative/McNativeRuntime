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
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftTitlePacket;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class MinecraftTitlePacketCodecV1_8 implements MinecraftPacketCodec<MinecraftTitlePacket> {

    @Override
    public void read(MinecraftTitlePacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        MinecraftTitlePacket.Action action = MinecraftTitlePacket.Action.of(connection.getProtocolVersion(),MinecraftProtocolUtil.readVarInt(buffer));
        packet.setAction(action);
        if(action == MinecraftTitlePacket.Action.SET_TITLE || action == MinecraftTitlePacket.Action.SET_SUBTITLE || action == MinecraftTitlePacket.Action.SET_ACTIONBAR){
            packet.setMessage(Text.decompile(MinecraftProtocolUtil.readString(buffer)));
        }else if(action == MinecraftTitlePacket.Action.SET_TIME){
            int[] data = new int[3];
            data[0] = buffer.readInt();
            data[1] = buffer.readInt();
            data[2] = buffer.readInt();
            packet.setTime(data);
        }
    }

    @Override
    public void write(MinecraftTitlePacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        MinecraftProtocolUtil.writeVarInt(buffer,packet.getAction().getId(connection.getProtocolVersion()));
        if(packet.getRawData() != null){
            if(packet.getRawData() instanceof MessageComponent){
                MinecraftProtocolUtil.writeString(buffer,((MessageComponent<?>)packet.getRawData())
                        .compileToString(connection.getProtocolVersion(),packet.getVariables()!=null?packet.getVariables(): VariableSet.createEmpty()));
            }else if(packet.getRawData() instanceof int[]){
                int[] array = (int[]) packet.getRawData();
                buffer.writeInt(array[0]);
                buffer.writeInt(array[1]);
                buffer.writeInt(array[2]);
            }else throw new IllegalArgumentException("Invalid data type.");
        }
    }
}
