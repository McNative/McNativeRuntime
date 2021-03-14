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

package org.mcnative.runtime.protocol.java.netty.rewrite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.libraries.utility.reflect.UnsafeInstanceCreator;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.Endpoint;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.*;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.util.List;

/**
 * This is an implementation into the Netty framework for reading and modifying an incoming Minecraft package from the pipeline.
 */
public class MinecraftProtocolRewriteDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final PacketManager packetManager;
    private final Endpoint endpoint;
    private final PacketDirection direction;
    private final MinecraftConnection connection;

    public MinecraftProtocolRewriteDecoder(PacketManager packetManager, Endpoint endpoint, PacketDirection direction, MinecraftConnection connection) {
        this.packetManager = packetManager;
        this.endpoint = endpoint;
        this.direction = direction;
        this.connection = connection;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> output) {
        ByteBuf out = in.copy();

        int packetId = MinecraftProtocolUtil.readVarInt(in);
        this.handleInternalPacketManipulation(packetId,in);

        PacketRegistration registration = packetManager.getPacketRegistration(connection.getState(),direction,connection.getProtocolVersion(),packetId);
        if(registration != null){
            try {
                List<MinecraftPacketListener> listeners = packetManager.getPacketListeners(endpoint,direction,registration.getPacketClass());
                if(listeners != null && !listeners.isEmpty()){
                    MinecraftPacketCodec codec = registration.getCodec(direction,connection.getState(),connection.getProtocolVersion());
                    MinecraftPacket packet = UnsafeInstanceCreator.newInstance(registration.getPacketClass());
                    codec.read(packet,connection,direction,in);

                    MinecraftPacketEvent event = new MinecraftPacketEvent(endpoint,direction,connection,packet);
                    listeners.forEach(listener -> listener.handle(event));

                    if(event.isCancelled()) return;
                    else if(event.isRewrite()){
                        packet = event.getPacket();
                        out.clear();
                        MinecraftProtocolUtil.writeVarInt(out,packetId);
                        codec.write(packet,connection,direction,in);
                    }
                }
            } catch (Exception e) {
                System.out.println("Packet:" + packetId + ":" + registration.getPacketClass());
                e.printStackTrace();
            }
        }
        output.add(out);
    }

    public void handleInternalPacketManipulation(int packetId,ByteBuf buffer){
        //Unused, but can optionally be implemented
    }
}
