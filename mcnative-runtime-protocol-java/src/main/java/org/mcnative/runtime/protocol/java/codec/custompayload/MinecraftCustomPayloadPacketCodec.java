package org.mcnative.runtime.protocol.java.codec.custompayload;

import org.mcnative.runtime.api.connection.ConnectionState;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftCustomPayloadPacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftCustomPayloadPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftCustomPayloadPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x3F,new MinecraftCustomPayloadPacketCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x18)
                        ,map(MinecraftProtocolVersion.JE_1_13,0x19)
                        ,map(MinecraftProtocolVersion.JE_1_14,0x18)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x19)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x18)
                        ,map(MinecraftProtocolVersion.JE_1_16_3,0x17))
                ,on(PacketDirection.OUTGOING, ConnectionState.LOGIN
                        ,map(MinecraftProtocolVersion.JE_1_7,0x3F,new MinecraftCustomPayloadPacketCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x18)
                        ,map(MinecraftProtocolVersion.JE_1_13,0x19)
                        ,map(MinecraftProtocolVersion.JE_1_14,0x18)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x19)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x18)
                        ,map(MinecraftProtocolVersion.JE_1_16_3,0x17))));
    }

}
