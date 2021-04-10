package org.mcnative.runtime.protocol.java.codec.player.headerandfooter;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.player.MinecraftPlayerListHeaderAndFooterPacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftPlayerListHeaderAndFooterPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftPlayerListHeaderAndFooterPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_8,0x47,new MinecraftPlayerListHeaderAndFooterPacketV1_8())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x48)
                        ,map(MinecraftProtocolVersion.JE_1_12,0x49)
                        ,map(MinecraftProtocolVersion.JE_1_12_1,0x4A)
                        ,map(MinecraftProtocolVersion.JE_1_12_2,0x53)
                        ,map(MinecraftProtocolVersion.JE_1_13,0x4E)
                        ,map(MinecraftProtocolVersion.JE_1_14,0x53)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x54)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x53))));
    }

}
