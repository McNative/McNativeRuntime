package org.mcnative.runtime.protocol.java.codec.disconnect;

import org.mcnative.runtime.api.connection.ConnectionState;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftDisconnectPacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftDisconnectPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftDisconnectPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x40,new MinecraftDisconnectPacketCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x1A)
                        ,map(MinecraftProtocolVersion.JE_1_13,0x1B)
                        ,map(MinecraftProtocolVersion.JE_1_14,0x1A)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x1B)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x1A)
                        ,map(MinecraftProtocolVersion.JE_1_16_3,0x19)
                        ,map(MinecraftProtocolVersion.JE_1_17,0x1A))
                ,on(PacketDirection.OUTGOING, ConnectionState.LOGIN
                        ,map(MinecraftProtocolVersion.JE_1_7,0x00,new MinecraftDisconnectPacketCodecV1_7()))));
    }

}
