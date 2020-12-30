package org.mcnative.runtime.protocol.java.version.v1_16;

import org.mcnative.runtime.api.connection.ConnectionState;
import org.mcnative.runtime.api.protocol.MinecraftProtocol;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatCodecV1_16;

public class MinecraftProtocolV1_16 {

    /*
    public static void register(){


        MinecraftChatPacket.IDENTIFIER.register();

        public final static PacketIdentifier IDENTIFIER = newIdentifier(MinecraftChatPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x02,null)


        MinecraftProtocol.registerCodec(Tripple(VERSIO, StatCODEC), Pair...)
        MinecraftProtocol.registerDefinition(MinecraftProtocolVersion.JE_1_16)
                .registerState(ConnectionState.GAME)
                    .registerIncoming(MinecraftChatPacket.class,0x0E,new MinecraftChatCodecV1_16());
    }
     */

}
