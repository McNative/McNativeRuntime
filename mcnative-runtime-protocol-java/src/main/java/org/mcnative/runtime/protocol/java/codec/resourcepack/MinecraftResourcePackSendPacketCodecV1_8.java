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

package org.mcnative.runtime.protocol.java.codec.resourcepack;

import io.netty.buffer.ByteBuf;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftDisconnectPacket;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftResourcePackSendPacket;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class MinecraftResourcePackSendPacketCodecV1_8 implements MinecraftPacketCodec<MinecraftResourcePackSendPacket> {

    @Override
    public void read(MinecraftResourcePackSendPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        packet.setUrl(MinecraftProtocolUtil.readString(buffer));
        packet.setHash(MinecraftProtocolUtil.readString(buffer));
    }

    @Override
    public void write(MinecraftResourcePackSendPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        MinecraftProtocolUtil.writeString(buffer,packet.getUrl());
        MinecraftProtocolUtil.writeString(buffer,packet.getHash());
    }
}
