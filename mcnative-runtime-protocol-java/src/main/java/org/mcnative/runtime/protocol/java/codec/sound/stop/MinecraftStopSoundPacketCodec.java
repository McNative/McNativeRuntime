package org.mcnative.runtime.protocol.java.codec.sound.stop;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.sound.MinecraftStopSoundPacket;
import org.mcnative.runtime.protocol.java.codec.sound.effect.MinecraftSoundEffectCodecV1_10;
import org.mcnative.runtime.protocol.java.codec.sound.effect.MinecraftSoundEffectCodecV1_9;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftStopSoundPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftStopSoundPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_13,0x4C,new MinecraftStopSoundCodecV1_13())
                        ,map(MinecraftProtocolVersion.JE_1_14,0x52)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x53)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x52)
                        ,map(MinecraftProtocolVersion.JE_1_17,0x5C))));
    }

}
