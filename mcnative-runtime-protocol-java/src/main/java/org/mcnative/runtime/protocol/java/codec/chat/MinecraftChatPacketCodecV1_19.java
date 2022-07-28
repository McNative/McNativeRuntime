package org.mcnative.runtime.protocol.java.codec.chat;

import io.netty.buffer.ByteBuf;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import org.mcnative.actionframework.sdk.common.protocol.codec.BufferUtil;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.player.chat.ChatPosition;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftChatPacket;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Justin_SGD
 * @since 28.07.2022
 */

public class MinecraftChatPacketCodecV1_19 implements MinecraftPacketCodec<MinecraftChatPacket> {

    private final static UUID EMPTY = new UUID(0,0);

    @Override
    public void read(MinecraftChatPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            packet.setMessage(Text.decompile(MinecraftProtocolUtil.readString(buffer)));
            packet.setPosition(ChatPosition.of(buffer.readByte()));
        }else if(direction == PacketDirection.INCOMING){
            packet.setMessage(Text.of(MinecraftProtocolUtil.readString(buffer)));
            packet.setTimestamp(Instant.ofEpochMilli(buffer.readLong()));
            packet.setSalt(buffer.readLong());
            packet.setSignatureLength(MinecraftProtocolUtil.readVarInt(buffer));
            packet.setSignature(MinecraftProtocolUtil.readArray(buffer));
            packet.setSignedPreview(buffer.readBoolean());
        }
    }

    @Override
    public void write(MinecraftChatPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(direction == PacketDirection.OUTGOING){
            Language language = null;
            if(connection instanceof LanguageAble) language = ((LanguageAble) connection).getLanguage();
            VariableSet variables = packet.getVariables()!=null?packet.getVariables():VariableSet.createEmpty();

            MinecraftProtocolUtil.writeString(buffer,packet.getMessage().compileToString(connection,variables,language));
            buffer.writeByte(packet.getPosition().getId());
        }else if(direction == PacketDirection.INCOMING){
            MinecraftProtocolUtil.writeString(buffer,packet.getMessage().toPlainText());
            buffer.writeLong(packet.getTimestamp().toEpochMilli());
            buffer.writeLong(packet.getSalt());
            MinecraftProtocolUtil.writeVarInt(buffer, packet.getSignatureLength());
            MinecraftProtocolUtil.writeArray(buffer, packet.getSignature());
            buffer.writeBoolean(packet.isSignedPreview());
        }
    }
}