/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.12.19, 15:24
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
import io.netty.handler.codec.MessageToByteEncoder;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.libraries.utility.reflect.UnsafeInstanceCreator;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.Endpoint;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.*;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;
import org.mcnative.runtime.protocol.java.netty.PacketCanceledException;

import java.util.List;

/**
 * This is an implementation into the Netty framework for reading and modifying an outgoing Minecraft package from the pipeline.
 */
public class MinecraftProtocolRewriteEncoder extends MessageToByteEncoder<ByteBuf> {

    private final PacketManager packetManager;
    private final Endpoint endpoint;
    private final PacketDirection direction;
    private final MinecraftConnection connection;
    private final MinecraftProtocolVersion version;

    public MinecraftProtocolRewriteEncoder(PacketManager packetManager, Endpoint endpoint, PacketDirection direction, MinecraftConnection connection) {
        this.packetManager = packetManager;
        this.endpoint = endpoint;
        this.direction = direction;
        this.connection = connection;
        this.version = connection.getProtocolVersion();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out) throws Exception {
        int packetId = MinecraftProtocolUtil.readVarInt(in);

        PacketRegistration registration = packetManager.getPacketRegistration(connection.getState(),direction,version,packetId);
        MinecraftProtocolUtil.writeVarInt(out,packetId);

        if(registration != null){
            Pair<Integer,MinecraftPacketCodec> data = registration.getCodecData(direction,connection.getState(),version);

            MinecraftPacketCodec codec = data.getValue();
            List<MinecraftPacketListener> listeners = packetManager.getPacketListeners(endpoint,direction, registration.getPacketClass());
            if(listeners != null && !listeners.isEmpty()){
                MinecraftPacket packet = UnsafeInstanceCreator.newInstance(registration.getPacketClass());
                in.markReaderIndex();
                codec.read(packet,connection,direction,in);

                MinecraftPacketEvent event = new MinecraftPacketEvent(endpoint,direction,connection,packet);
                listeners.forEach(listener -> listener.handle(event));
                if(event.isCancelled()) throw new PacketCanceledException();

                if(event.isRewrite()){
                    packet = event.getPacket();
                    codec.write(packet,connection,direction,out);
                    return;
                }else in.resetReaderIndex();
            }
        }
        out.writeBytes(in);
    }
}
