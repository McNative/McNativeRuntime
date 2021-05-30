package org.mcnative.runtime.protocol.java.codec;

import io.netty.buffer.ByteBuf;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

public class LegacyTabCompleteForce {
    
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
