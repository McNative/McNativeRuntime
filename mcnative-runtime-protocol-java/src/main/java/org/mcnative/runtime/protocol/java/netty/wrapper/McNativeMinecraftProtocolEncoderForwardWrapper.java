/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.05.20, 21:15
 * @web %web%
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

package org.mcnative.runtime.protocol.java.netty.wrapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import net.pretronic.libraries.utility.reflect.ReflectionUtil;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacket;
import org.mcnative.runtime.protocol.java.netty.MinecraftProtocolEncoder;

import java.lang.reflect.Method;

public class McNativeMinecraftProtocolEncoderForwardWrapper extends ChannelOutboundHandlerAdapter {

    private final static Method ENCODE_METHOD = ReflectionUtil.getMethod(MessageToByteEncoder.class,"encode"
            ,new Class[]{ChannelHandlerContext.class,Object.class, ByteBuf.class});

    static {
        ENCODE_METHOD.setAccessible(true);
    }

    private final MinecraftProtocolEncoder encoder;
    private final MessageToByteEncoder<Object> original;

    public McNativeMinecraftProtocolEncoderForwardWrapper(MinecraftProtocolEncoder encoder, MessageToByteEncoder<Object> original) {
        this.encoder = encoder;
        this.original = original;
    }

    public MessageToByteEncoder<Object> getOriginal() {
        return original;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof MinecraftPacket){
            encoder.write(ctx, msg, promise);
        }else {
            if(original.acceptOutboundMessage(msg)){
                ByteBuf buffer = Unpooled.directBuffer();
                ENCODE_METHOD.invoke(original,ctx,msg,buffer);
                ctx.write(buffer, promise);
            }else{
                ctx.write(msg,promise);
            }
        }
    }


}
