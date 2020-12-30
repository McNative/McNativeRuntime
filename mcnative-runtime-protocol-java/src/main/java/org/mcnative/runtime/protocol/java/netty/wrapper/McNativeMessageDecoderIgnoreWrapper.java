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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.pretronic.libraries.utility.reflect.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class McNativeMessageDecoderIgnoreWrapper extends ChannelInboundHandlerAdapter {

    private final static Method DECODE_METHOD = ReflectionUtil.getMethod(ByteToMessageDecoder.class,"decode"
            ,new Class[]{ChannelHandlerContext.class,ByteBuf.class, List.class});

    static {
        DECODE_METHOD.setAccessible(true);
    }

    private final ByteToMessageDecoder original;

    public McNativeMessageDecoderIgnoreWrapper(ByteToMessageDecoder original) {
        this.original = original;
    }

    public ByteToMessageDecoder getOriginal() {
        return original;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            List<Object> out = new ArrayList<>();

            if(msg instanceof ByteBuf){
                DECODE_METHOD.invoke(original,ctx, msg,out);
                for (Object o : out) {
                    ctx.fireChannelRead(o);
                }
            }else{
                ctx.write(msg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
