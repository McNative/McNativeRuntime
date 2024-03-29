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

package org.mcnative.runtime.protocol.java.codec.complete.response;

import io.netty.buffer.ByteBuf;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.player.complete.MinecraftPlayerTabCompleteResponsePacket;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.util.ArrayList;

public class MinecraftTabCompleteResponsePacketCodecV1_7 implements MinecraftPacketCodec<MinecraftPlayerTabCompleteResponsePacket> {

    @Override
    public void read(MinecraftPlayerTabCompleteResponsePacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            packet.setSuggestions(MinecraftProtocolUtil.readStringArray(buffer));
        }
    }

    @Override
    public void write(MinecraftPlayerTabCompleteResponsePacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            MinecraftProtocolUtil.writeStringArray(buffer,new ArrayList<>(packet.getSuggestions()));
        }
    }
}
