package org.mcnative.runtime.protocol.java.codec.complete.request;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.api.protocol.packet.type.player.complete.MinecraftPlayerTabCompletePacket;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatPacketCodecV1_16;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatPacketCodecV1_7;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatPacketCodecV1_8;
import org.mcnative.runtime.protocol.java.codec.sound.effect.MinecraftSoundEffectCodecV1_10;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftTabCompleteRequestPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftPlayerTabCompletePacket.class
                ,on(PacketDirection.INCOMING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x14,new MinecraftTabCompleteRequestPacketCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x01,new MinecraftTabCompleteRequestPacketCodecV1_9())
                        ,map(MinecraftProtocolVersion.JE_1_13,0x05,new MinecraftTabCompleteRequestPacketCodecV1_13())
                        ,map(MinecraftProtocolVersion.JE_1_14,0x06)
                        ,map(MinecraftProtocolVersion.JE_1_19, 0x08))));
    }

}
