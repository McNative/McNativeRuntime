/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.09.19, 17:59
 *
 * The McNative Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.mcnative.runtime.protocol.java;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class contains various utilities for writing and reading Minecraft packages.
 * More information and documentation: https://wiki.vg/Protocol
 */
public class MinecraftProtocolUtil {

    public static void writeString(ByteBuf buffer, String message) {
        if(message.length() > Short.MAX_VALUE) throw new IllegalArgumentException(String.format("String is longer than Short.MAX_VALUE (%s characters)",message.length()));
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buffer,data.length);
        buffer.writeBytes(data);
    }

    public static String readString(ByteBuf buffer){
        int length = readVarInt(buffer);
        if(length > Short.MAX_VALUE) throw new IllegalArgumentException(String.format("String is longer than Short.MAX_VALUE (%s characters)",length));

        byte[] data = new byte[length];
        buffer.readBytes(data);
        return new String(data,StandardCharsets.UTF_8);
    }


    public static int readVarInt(ByteBuf input){
        return readVarInt( input, 5 );
    }

    public static int readVarInt(ByteBuf buffer, int maximumLength) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = buffer.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static void writeVarInt(ByteBuf buffer, int value) {
        int part;
        do {
            part = value & 0x7F;

            value >>>= 7;
            if(value != 0) part |= 0x80;

            buffer.writeByte(part);
        } while (value != 0);
    }

    public static void writeUnsignedInt(ByteBuf buffer, int value) {
        buffer.writeInt(value & 0xFF);
    }


    public static int readVarShort(ByteBuf buffer) {
        int low = buffer.readUnsignedShort();
        int high = 0;
        if((low & 0x8000 ) != 0) {
            low = low & 0x7FFF;
            high = buffer.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = ( toWrite & 0x7F8000 ) >> 15;
        if(high != 0 ) low = low | 0x8000;

        buf.writeShort( low );
        if(high != 0 ) buf.writeByte( high );
    }


    public static UUID readUUID(ByteBuf buffer)
    {
        return new UUID(buffer.readLong(),buffer.readLong() );
    }

    public static void writeUUID(ByteBuf buffer,UUID value) {
        buffer.writeLong(value.getMostSignificantBits());
        buffer.writeLong(value.getLeastSignificantBits());
    }


    public static byte[] readArray(ByteBuf buffer) {
        return readArray(buffer,buffer.readableBytes());
    }

    public static byte[] readArray(ByteBuf buf, int maximum)
    {
        int length = readVarInt( buf );
        if(length > maximum) throw new IllegalArgumentException(String.format("Array is longer than Short.MAX_VALUE (%s characters)",length));
        byte[] data = new byte[ length ];
        buf.readBytes(data);
        return data;
    }

    public static void writeArray(ByteBuf buffer,byte[] value) {
        if (value.length > Short.MAX_VALUE ) throw new IllegalArgumentException(String.format("Array is longer than Short.MAX_VALUE (%s characters)",value.length));
        writeVarInt(buffer,value.length);
        buffer.writeBytes(value);
    }

    public static byte[] toArray(ByteBuf buffer) {
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        return data;
    }

    public static int[] readVarIntArray(ByteBuf buffer) {
        int length = readVarInt(buffer);
        int[] data = new int[length];

        for (int i = 0; i < length; i++ ) data[i] = readVarInt(buffer);

        return data;
    }

    public static List<String> readStringArray(ByteBuf buffer) {
        int length = readVarInt(buffer);
        List<String> result = new ArrayList<>(length);

        for (int i = 0; i < length; i++ ) result.add(readString(buffer) );

        return result;
    }

    public static void writeStringArray(ByteBuf buffer,List<String> data) {
        if(data == null) writeVarInt(buffer,0);
        else{
            writeVarInt(buffer,data.size());
            for ( String message : data) writeString(buffer,message);
        }
    }

    public static void writeStringArray(ByteBuf buffer,String[] data) {
        if(data == null) writeVarInt(buffer,0);
        else{
            writeVarInt(buffer,data.length);
            for (String message : data) writeString(buffer,message);
        }
    }
}
