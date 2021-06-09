package org.mcnative.runtime.protocol.java.codec.title;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftTitlePacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftTitlePacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftTitlePacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_8,0x45,new MinecraftTitlePacketCodecV1_8())
                        ,map(MinecraftProtocolVersion.JE_1_12,0x47)
                        ,map(MinecraftProtocolVersion.JE_1_12_1,0x48)
                        ,map(MinecraftProtocolVersion.JE_1_13,0x4B)
                        ,map(MinecraftProtocolVersion.JE_1_14,0x4F)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x50)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x4F)
                        ,map(MinecraftProtocolVersion.JE_1_17,0x56,new MinecraftTitlePacketCodecV1_17()))));
    }

}
