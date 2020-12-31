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

package org.mcnative.runtime.protocol.java.codec.player.headerandfooter;

import io.netty.buffer.ByteBuf;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftDisconnectPacket;
import org.mcnative.runtime.api.protocol.packet.type.player.MinecraftPlayerListHeaderAndFooterPacket;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class MinecraftPlayerListHeaderAndFooterPV1_8 implements MinecraftPacketCodec<MinecraftPlayerListHeaderAndFooterPacket> {

    private static final String REMOVE_CONTENT = "{\"translate\":\"\"}";

    @Override
    public void read(MinecraftPlayerListHeaderAndFooterPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        //Currently not implemented, should be integrated for packet manipulation
    }

    @Override
    public void write(MinecraftPlayerListHeaderAndFooterPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        String header = packet.getHeader() == null ? REMOVE_CONTENT : packet.getHeader().compileToString(connection.getProtocolVersion(),packet.getHeaderVariables());
        String footer = packet.getFooter() == null ? REMOVE_CONTENT : packet.getFooter().compileToString(connection.getProtocolVersion(),packet.getFooterVariables());

        MinecraftProtocolUtil.writeString(buffer, header);
        MinecraftProtocolUtil.writeString(buffer, footer);
    }
}
