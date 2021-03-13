package org.mcnative.runtime.protocol.java.codec.sound.effect;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.sound.MinecraftSoundEffectPacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftSoundEffectPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftSoundEffectPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x29,new MinecraftSoundEffectCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x19,new MinecraftSoundEffectCodecV1_9())
                        ,map(MinecraftProtocolVersion.JE_1_10,0x19,new MinecraftSoundEffectCodecV1_10())
                        ,map(MinecraftProtocolVersion.JE_1_13,0x1A)
                        ,map(MinecraftProtocolVersion.JE_1_14,0x19)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x1A)
                        ,map(MinecraftProtocolVersion.JE_1_16,0x19)
                        ,map(MinecraftProtocolVersion.JE_1_16_2,0x18))));
    }

}
