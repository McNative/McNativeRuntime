package org.mcnative.runtime.protocol.java.codec.scoreboard.team;

import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.api.protocol.packet.type.scoreboard.MinecraftScoreboardTeamsPacket;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatPacketCodecV1_16;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatPacketCodecV1_7;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatPacketCodecV1_8;

import static org.mcnative.runtime.api.protocol.packet.PacketRegistration.*;

public class MinecraftScoreboardTeamPacketCodec {

    public static void register(PacketManager manager){
        manager.registerPacket(create(MinecraftScoreboardTeamsPacket.class
                ,on(PacketDirection.OUTGOING
                        ,map(MinecraftProtocolVersion.JE_1_7,0x3E,new MinecraftScoreboardTeamPacketCodecV1_7())
                        ,map(MinecraftProtocolVersion.JE_1_9,0x41,new MinecraftScoreboardTeamPacketCodecV1_9())
                        ,map(MinecraftProtocolVersion.JE_1_10,0x41,new MinecraftScoreboardTeamPacketCodecV1_10())
                        ,map(MinecraftProtocolVersion.JE_1_12,0x43)
                        ,map(MinecraftProtocolVersion.JE_1_12_1,0x44)
                        ,map(MinecraftProtocolVersion.JE_1_13,0x47,new MinecraftScoreboardTeamPacketCodecV1_13())
                        ,map(MinecraftProtocolVersion.JE_1_14,0x4B)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x4C)
                        ,map(MinecraftProtocolVersion.JE_1_15,0x4C)
                        ,map(MinecraftProtocolVersion.JE_1_17,0x55))));
    }

    protected static String substringName(String name){
        if(name.length() > 16) return name.substring(0,16);
        return name;
    }

}
