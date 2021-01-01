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
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.scoreboard.MinecraftScoreboardTeamsPacket;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class MinecraftScoreboardTeamPacketCodecV1_13 implements MinecraftPacketCodec<MinecraftScoreboardTeamsPacket> {

    @Override
    public void read(MinecraftScoreboardTeamsPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        //Currently not implemented, should be integrated for packet manipulation
    }

    @Override
    public void write(MinecraftScoreboardTeamsPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            MinecraftProtocolUtil.writeString(buffer,packet.getName());
            buffer.writeByte(packet.getAction().ordinal());

            if(packet.getAction() == MinecraftScoreboardTeamsPacket.Action.CREATE || packet.getAction() == MinecraftScoreboardTeamsPacket.Action.UPDATE){

                MinecraftProtocolUtil.writeString(buffer, packet.getDisplayName() == null ? "{}" : packet.getDisplayName().compileToString(connection.getProtocolVersion(),packet.getVariables()));
                buffer.writeByte(packet.getFriendlyFlag().getCode());
                MinecraftProtocolUtil.writeString(buffer,packet.getNameTagVisibility().getNameTagVisibilityName());
                MinecraftProtocolUtil.writeString(buffer,packet.getCollisionRule().getCollisionRuleName());
                MinecraftProtocolUtil.writeVarInt(buffer,packet.getColor().getClientCode());
                MinecraftProtocolUtil.writeString(buffer, packet.getPrefix() == null ? "{}" : packet.getPrefix().compileToString(connection.getProtocolVersion(),packet.getVariables()));
                MinecraftProtocolUtil.writeString(buffer, packet.getSuffix() == null ? "{}" : packet.getSuffix().compileToString(connection.getProtocolVersion(),packet.getVariables()));

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
}