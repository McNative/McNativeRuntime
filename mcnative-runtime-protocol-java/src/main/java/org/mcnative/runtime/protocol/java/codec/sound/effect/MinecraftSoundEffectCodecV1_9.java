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

package org.mcnative.runtime.protocol.java.codec.sound.effect;

import io.netty.buffer.ByteBuf;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.player.sound.SoundCategory;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.sound.MinecraftSoundEffectPacket;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class MinecraftSoundEffectCodecV1_9 implements MinecraftPacketCodec<MinecraftSoundEffectPacket> {

    @Override
    public void read(MinecraftSoundEffectPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        packet.setSoundName(MinecraftProtocolUtil.readString(buffer));
        packet.setCategory(SoundCategory.MASTER);
        packet.setPositionX(buffer.readInt());
        packet.setPositionY(buffer.readInt());
        packet.setPositionZ(buffer.readInt());
        packet.setVolume(buffer.readFloat());
        packet.setPitch(buffer.readByte());
    }

    @Override
    public void write(MinecraftSoundEffectPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        MinecraftProtocolUtil.writeString(buffer,packet.getSoundName());
        MinecraftProtocolUtil.writeVarInt(buffer,packet.getCategory().ordinal());
        buffer.writeInt(packet.getPositionX()*8);
        buffer.writeInt(packet.getPositionY()*8);
        buffer.writeInt(packet.getPositionZ()*8);
        buffer.writeFloat(MinecraftSoundEffectPacketCodec.getVolume(packet.getVolume()));
        MinecraftProtocolUtil.writeUnsignedInt(buffer,(int)packet.getPitch());
    }
}
