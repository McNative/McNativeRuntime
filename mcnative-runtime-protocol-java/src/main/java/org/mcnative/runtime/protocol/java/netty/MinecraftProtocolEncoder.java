/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.09.19, 16:20
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

package org.mcnative.runtime.protocol.java.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.Endpoint;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.*;

import java.util.List;

/**
 * This is a implementation into the netty framework to encode and write Minecraft packets.
 */
public class MinecraftProtocolEncoder extends MessageToByteEncoder<MinecraftPacket> {

    private final PacketManager packetManager;
    private final Endpoint endpoint;
    private final PacketDirection direction;
    private final MinecraftConnection connection;
    private final MinecraftProtocolVersion version;

    public MinecraftProtocolEncoder(PacketManager packetManager, Endpoint endpoint, PacketDirection direction, MinecraftConnection connection) {
        this.packetManager = packetManager;
        this.endpoint = endpoint;
        this.direction = direction;
        this.connection = connection;
        this.version = connection.getProtocolVersion();
    }

    public MinecraftProtocolVersion getVersion() {
        return version;
    }

    public int getProtocolNumber(){
        return getVersion().getNumber();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(ChannelHandlerContext context, MinecraftPacket packet0, ByteBuf buffer){
        try {
            MinecraftPacket packet = packet0;
            List<? extends MinecraftPacketListener> listeners = packetManager.getPacketListeners(endpoint,direction,packet.getClass());
            if(listeners != null && !listeners.isEmpty()){
                MinecraftPacketEvent event = new MinecraftPacketEvent(endpoint,direction,connection,packet);
                listeners.forEach(listener -> listener.handle(event));
                if(event.isCancelled()) throw new PacketCanceledException();
                packet = event.getPacket();
            }

            PacketRegistration registration = packetManager.getPacketRegistration(packet.getClass());
            Pair<Integer,MinecraftPacketCodec> data = registration.getCodecData(direction,connection.getState(),version);

            MinecraftProtocolUtil.writeVarInt(buffer,data.getKey());
            data.getValue().write(packet,connection,direction,buffer);
        } catch (Exception exception) {
            McNative.getInstance().getLogger().error("An error occurred in McNative:", exception.getMessage());
            exception.printStackTrace();
        }
    }
}
