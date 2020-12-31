package org.mcnative.runtime.protocol.java.codec.resourcepack;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftResourcePackSendPacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftResourcePackSendPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftResourcePackSendPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_8,0x48,new MinecraftResourcePackSendPacketCodecV1_8())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x32)
                        ,map(MinecraftProtocolVersion.JE_1_12,0x33)
                        ,map(MinecraftProtocolVersion.JE_1_13,0x37)
                        ,map(MinecraftProtocolVersion.JE_1_14,0x39)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x3A)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x39)
                        ,map(MinecraftProtocolVersion.JE_1_16_3,0x38))));
    }

}
