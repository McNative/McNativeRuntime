package org.mcnative.runtime.protocol.java.version.v1_16;

import org.mcnative.runtime.api.connection.ConnectionState;
import org.mcnative.runtime.api.protocol.MinecraftProtocol;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;

public class MinecraftProtocolV1_16 {

    public static void register(){
        MinecraftProtocol.registerDefinition(MinecraftProtocolVersion.JE_1_16)
                .registerState(ConnectionState.GAME)
                    .registerIncoming(MinecraftChatPacket.class,0x0E,new MinecraftChatCodecV1_16());
    }

}
