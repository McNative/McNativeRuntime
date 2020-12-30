/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.07.20, 15:35
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

package org.mcnative.runtime.protocol.java;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.ServerStatusRequester;
import org.mcnative.runtime.api.network.component.server.ServerStatusResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class JavaServerStatusRequester implements ServerStatusRequester {

    private static final byte PACKET_HANDSHAKE = 0x00;
    private static final byte PACKET_STATUS_REQUEST = PACKET_HANDSHAKE;
    private static final byte PACKET_PING = 0x01;
    private static final int STATUS_HANDSHAKE = 1;

    public CompletableFuture<ServerStatusResponse> requestStatusAsync(MinecraftServer server){
        return requestStatusAsync(server.getAddress());
    }

    public CompletableFuture<ServerStatusResponse> requestStatusAsync(InetSocketAddress address){
        CompletableFuture<ServerStatusResponse> future = new CompletableFuture<>();
        McNative.getInstance().getExecutorService().execute(() -> {
            try{
                future.complete(requestStatus(address));
            }catch (Throwable e){
                future.completeExceptionally(e);
            }
        });
        return future;
    }


    public ServerStatusResponse requestStatus(MinecraftServer server){
        return requestStatus(server.getAddress());
    }

    public ServerStatusResponse requestStatus(InetSocketAddress address){
        try{
            final Socket socket = new Socket();
            socket.connect(address,3000);

            ByteBuf buffer = Unpooled.buffer();

            buffer.writeByte(PACKET_HANDSHAKE);
            MinecraftProtocolUtil.writeVarInt(buffer,1);
            MinecraftProtocolUtil.writeString(buffer, address.getAddress().getHostName());
            buffer.writeShort(address.getPort());
            MinecraftProtocolUtil.writeVarInt(buffer,STATUS_HANDSHAKE);

            socket.getOutputStream().write(buffer.readableBytes());
            socket.getOutputStream().write(buffer.array());

            //Request status
            socket.getOutputStream().write(0x01);
            socket.getOutputStream().write(PACKET_STATUS_REQUEST);

            readToBuffer(buffer,socket.getInputStream());

            int id = MinecraftProtocolUtil.readVarInt(buffer);

            if(id == -1) throw new OperationFailedException("Server prematurely ended stream.");
            if(id != PACKET_STATUS_REQUEST) throw new OperationFailedException("Server returned invalid packet.");

            String statusResponse = MinecraftProtocolUtil.readString(buffer);

            //Ping
            long now = System.currentTimeMillis();
            buffer.clear();
            buffer.writeByte(PACKET_PING);
            buffer.writeLong(now);

            socket.getOutputStream().write(buffer.readableBytes());
            socket.getOutputStream().write(buffer.array());

            readToBuffer(buffer,socket.getInputStream());

            id = MinecraftProtocolUtil.readVarInt(buffer);
            long ping = System.currentTimeMillis()-now;
            if(id == -1) throw new OperationFailedException("Server prematurely ended stream.");
            if(id != PACKET_PING) throw new OperationFailedException("Server returned invalid packet.");
            buffer.release();
            socket.close();

            ServerStatusResponse response =  ServerStatusResponse.newServerStatusResponse();
            response.read(statusResponse);
            response.setPing((int) ping);
            return response;
        }catch (Exception e){
            throw new OperationFailedException("Status request failed",e);
        }
    }

    private static void readToBuffer(ByteBuf buffer, InputStream input) throws Exception{
        buffer.clear();
        int size =  readVarInt(input);
        byte[] temp = new byte[size];
        input.read(temp);
        buffer.writeBytes(temp);
    }

    private static int readVarInt(InputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.read();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128)
                break;
        }
        return i;
    }

}
