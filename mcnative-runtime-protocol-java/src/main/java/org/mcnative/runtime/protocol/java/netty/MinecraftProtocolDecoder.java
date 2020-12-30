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
import io.netty.handler.codec.MessageToMessageDecoder;
import net.pretronic.libraries.utility.reflect.UnsafeInstanceCreator;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.Endpoint;
import org.mcnative.runtime.api.protocol.definition.MinecraftProtocolData;
import org.mcnative.runtime.api.protocol.definition.MinecraftProtocolStateDefinition;
import org.mcnative.runtime.api.protocol.packet.*;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.util.List;

/**
 * This is a implementation into the netty framework to decode and read Minecraft packets.
 */
public class MinecraftProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final PacketManager packetManager;
    private final Endpoint endpoint;
    private final PacketDirection direction;
    private final MinecraftConnection connection;

    public MinecraftProtocolDecoder(PacketManager packetManager, Endpoint endpoint, PacketDirection direction, MinecraftConnection connection) {
        this.packetManager = packetManager;
        this.endpoint = endpoint;
        this.direction = direction;
        this.connection = connection;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> output) {
        try {
            int packetId = MinecraftProtocolUtil.readVarInt(in);

            MinecraftProtocolStateDefinition definition = connection.getProtocolDefinition();
            MinecraftProtocolData data = definition.getProtocolData(direction,packetId);
            if(data != null){
                MinecraftPacketCodec codec = data.getCodec();
                MinecraftPacket packet = UnsafeInstanceCreator.newInstance(data.getPacketClass());
                codec.read(packet,connection,direction,in);
                List<MinecraftPacketListener> listeners = packetManager.getPacketListeners(endpoint,direction,packet.getClass());
                if(listeners != null && !listeners.isEmpty()){
                    MinecraftPacketEvent event = new MinecraftPacketEvent(endpoint,direction,connection,packet);
                    listeners.forEach(listener -> listener.handle(event));
                    if(event.isCancelled()) return;
                    packet = event.getPacket();
                }
                output.add(packet);
            }
        } catch (Exception exception) {
            McNative.getInstance().getLogger().error("An error occurred in McNative:", exception.getMessage());
        }
    }
}
