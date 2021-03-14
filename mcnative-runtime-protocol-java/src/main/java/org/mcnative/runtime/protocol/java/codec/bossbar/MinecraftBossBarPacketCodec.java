package org.mcnative.runtime.protocol.java.codec.bossbar;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftBossBarPacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftBossBarPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftBossBarPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x0C,new MinecraftBossBarPacketCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_15,0x0D)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x0C))));
    }

}
