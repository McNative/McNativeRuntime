package org.mcnative.runtime.protocol.java;

import org.mcnative.runtime.api.protocol.packet.PacketManager;
import org.mcnative.runtime.protocol.java.codec.chat.MinecraftChatCodec;

public class MinecraftJavaProtocol {

    public static void register(PacketManager manager){
        MinecraftChatCodec.register(manager);
    }

}
