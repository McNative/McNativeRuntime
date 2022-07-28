package org.mcnative.runtime.protocol.java.codec.chat;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftChatPacketCodec {

    public static void register(PacketManager manager) {
        manager.registerPacket(create(MinecraftChatPacket.class
                , on(PacketDirection.OUTGOING
                        , map(MinecraftProtocolVersion.JE_1_7, 0x02, new MinecraftChatPacketCodecV1_7())
                        , map(MinecraftProtocolVersion.JE_1_8, 0x02, new MinecraftChatPacketCodecV1_8())
                        , map(MinecraftProtocolVersion.JE_1_9, 0x0F)
                        , map(MinecraftProtocolVersion.JE_1_13, 0x0E)
                        , map(MinecraftProtocolVersion.JE_1_15, 0x0F)
                        , map(MinecraftProtocolVersion.JE_1_16, 0x0E, new MinecraftChatPacketCodecV1_16())
                        , map(MinecraftProtocolVersion.JE_1_17, 0x0F)
                        , map(MinecraftProtocolVersion.JE_1_19, 0x5F, new MinecraftChatPacketCodecV1_19()))
                , on(PacketDirection.INCOMING
                        , map(MinecraftProtocolVersion.JE_1_7, 0x01, new MinecraftChatPacketCodecV1_7())
                        , map(MinecraftProtocolVersion.JE_1_9, 0x02)
                        , map(MinecraftProtocolVersion.JE_1_12, 0x03)
                        , map(MinecraftProtocolVersion.JE_1_12_1, 0x02)
                        , map(MinecraftProtocolVersion.JE_1_14, 0x03)
                        , map(MinecraftProtocolVersion.JE_1_19, 0x04, new MinecraftChatPacketCodecV1_19()))));
    }

}
