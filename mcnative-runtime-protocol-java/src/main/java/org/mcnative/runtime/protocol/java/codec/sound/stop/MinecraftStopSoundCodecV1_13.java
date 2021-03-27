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

package org.mcnative.runtime.protocol.java.codec.sound.stop;

import io.netty.buffer.ByteBuf;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.player.sound.MinecraftSound;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.sound.MinecraftSoundEffectPacket;
import org.mcnative.runtime.api.protocol.packet.type.sound.MinecraftStopSoundPacket;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.util.HashMap;
import java.util.Map;

public class MinecraftStopSoundCodecV1_13 implements MinecraftPacketCodec<MinecraftStopSoundPacket> {

    @Override
    public void read(MinecraftStopSoundPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        //@Todo implement
    }

    @Override
    public void write(MinecraftStopSoundPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        buffer.writeByte(packet.getAction().ordinal());
        if(packet.getAction() == MinecraftStopSoundPacket.Action.CATEGORY || packet.getAction() == MinecraftStopSoundPacket.Action.BOTH){
            MinecraftProtocolUtil.writeVarInt(buffer,packet.getCategory().ordinal());
        }
        if(packet.getAction() == MinecraftStopSoundPacket.Action.SOUND || packet.getAction() == MinecraftStopSoundPacket.Action.BOTH){
            MinecraftProtocolUtil.writeString(buffer,packet.getSoundName());
        }
    }
}
