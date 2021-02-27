package org.mcnative.runtime.protocol.java;

import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatPacketCodec;
import org.mcnative.runtime.protocol.java.codec.custompayload.CustomPayloadPacketCodec;
import org.mcnative.runtime.protocol.java.codec.disconnect.MinecraftDisconnectPacketCodec;
import org.mcnative.runtime.protocol.java.codec.player.headerandfooter.MinecraftPlayerListHeaderAndFooterPacketCodec;
import org.mcnative.runtime.protocol.java.codec.resourcepack.MinecraftResourcePackSendPacketCodec;
import org.mcnative.runtime.protocol.java.codec.scoreboard.team.MinecraftScoreboardTeamPacketCodec;
import org.mcnative.runtime.protocol.java.codec.title.MinecraftTitlePacketCodec;

public class MinecraftJavaProtocol {

    public static void register(PacketManager manager){
        MinecraftChatPacketCodec.register(manager);
        MinecraftTitlePacketCodec.register(manager);
        MinecraftDisconnectPacketCodec.register(manager);
        MinecraftScoreboardTeamPacketCodec.register(manager);
        MinecraftPlayerListHeaderAndFooterPacketCodec.register(manager);
        MinecraftResourcePackSendPacketCodec.register(manager);
        CustomPayloadPacketCodec.register(manager);
    }

}
