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

package org.mcnative.runtime.protocol.java.codec.scoreboard.team;

import io.netty.buffer.ByteBuf;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.scoreboard.MinecraftScoreboardTeamsPacket;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.api.text.format.TextColor;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class MinecraftScoreboardTeamPacketCodecV1_10 implements MinecraftPacketCodec<MinecraftScoreboardTeamsPacket> {

    @Override
    public void read(MinecraftScoreboardTeamsPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        //Currently not implemented, should be integrated for packet manipulation
    }

    @Override
    public void write(MinecraftScoreboardTeamsPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            MinecraftProtocolUtil.writeString(buffer,MinecraftScoreboardTeamPacketCodec.substringName(packet.getName()));
            buffer.writeByte(packet.getAction().ordinal());

            if(packet.getAction() == MinecraftScoreboardTeamsPacket.Action.CREATE || packet.getAction() == MinecraftScoreboardTeamsPacket.Action.UPDATE){

                MinecraftProtocolUtil.writeString(buffer, compileText(packet.getDisplayName(),connection,packet));
                MinecraftProtocolUtil.writeString(buffer, compileText(packet.getPrefix(),connection,packet));
                MinecraftProtocolUtil.writeString(buffer, compileText(packet.getSuffix(),connection,packet));
                buffer.writeByte(packet.getFriendlyFlag().ordinal());
                MinecraftProtocolUtil.writeString(buffer,packet.getNameTagVisibility().getNameTagVisibilityName());
                MinecraftProtocolUtil.writeString(buffer,packet.getCollisionRule().getCollisionRuleName());

                TextColor color = packet.getColor() != null ? packet.getColor() : TextColor.WHITE;
                buffer.writeByte(color.getClientCode());

                if(packet.getAction() == MinecraftScoreboardTeamsPacket.Action.CREATE) {
                    MinecraftProtocolUtil.writeStringArray(buffer, packet.getEntities());
                }
            }else if(packet.getAction() == MinecraftScoreboardTeamsPacket.Action.ADD_ENTITIES){
                MinecraftProtocolUtil.writeStringArray(buffer, packet.getEntities());
            }else if(packet.getAction() == MinecraftScoreboardTeamsPacket.Action.REMOVE_ENTITIES){
                MinecraftProtocolUtil.writeStringArray(buffer, packet.getEntities());
            }
        }
    }

    private String compileText(MessageComponent<?> component, MinecraftConnection connection, MinecraftScoreboardTeamsPacket packet) {
        if(component == null) return "";
        Language language = component instanceof LanguageAble ? ((LanguageAble) component).getLanguage() : null;
        String text = component.compileToString(connection,MinecraftProtocolVersion.JE_1_7,packet.getVariables(),language);
        if(text.length() > 16) return text.substring(0,16);
        return text;
    }
}
