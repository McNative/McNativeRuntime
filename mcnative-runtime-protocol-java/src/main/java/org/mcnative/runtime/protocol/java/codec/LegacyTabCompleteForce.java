package org.mcnative.runtime.protocol.java.codec;

import io.netty.buffer.ByteBuf;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class LegacyTabCompleteForce {

    public static boolean isDeclarePacket(MinecraftProtocolVersion version, int packetId){
        if(MinecraftProtocolVersion.JE_1_16_2.isNewerOrSame(version)) return 0x10 == packetId;
        else if(MinecraftProtocolVersion.JE_1_16.isNewerOrSame(version)) return 0x11 == packetId;
        else if(MinecraftProtocolVersion.JE_1_15.isNewerOrSame(version)) return 0x12 == packetId;
        else if(MinecraftProtocolVersion.JE_1_13.isNewerOrSame(version)) return 0x11 == packetId;
        else return false;
    }

    public static void rewrite(ByteBuf buffer){
        MinecraftProtocolUtil.writeVarInt(buffer,2);


        //node 0
        byte flags = 0;
        buffer.writeByte(flags);
        MinecraftProtocolUtil.writeVarInt(buffer,1);
        MinecraftProtocolUtil.writeVarInt(buffer,1);
        //end node

        //node 1
        byte flags2 = 2;
        flags2 |= 0x10;
        buffer.writeByte(flags2);
        MinecraftProtocolUtil.writeVarInt(buffer,0);
        MinecraftProtocolUtil.writeString(buffer,"ban");
        MinecraftProtocolUtil.writeString(buffer,"brigadier:string");
        MinecraftProtocolUtil.writeVarInt(buffer,2);
        MinecraftProtocolUtil.writeString(buffer,"minecraft:ask_server");
        //end node

        MinecraftProtocolUtil.writeVarInt(buffer,0);
    }

}
