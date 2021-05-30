package org.mcnative.runtime.protocol.java.codec.complete.response;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.player.complete.MinecraftPlayerTabCompleteResponsePacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftTabCompleteResponsePacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftPlayerTabCompleteResponsePacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x3A,new MinecraftTabCompleteResponsePacketCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_13,0x10,new MinecraftTabCompleteResponsePacketCodecV1_13())
                        ,map(MinecraftProtocolVersion.JE_1_15,0x11)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x10)
                        ,map(MinecraftProtocolVersion.JE_1_16_2,0x0F)
                        ,map(MinecraftProtocolVersion.JE_1_16_4,0x0F))));
    }

}
