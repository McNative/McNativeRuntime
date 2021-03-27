/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.09.19, 18:15
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

package org.mcnative.runtime.protocol.java.codec.bossbar;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.pretronic.libraries.concurrent.Task;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.connection.PendingConnection;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftBossBarPacket;
import org.mcnative.runtime.api.utils.positioning.Position;
import org.mcnative.runtime.api.utils.positioning.Vector;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class MinecraftBossBarPacketCodecV1_7 implements MinecraftPacketCodec<MinecraftBossBarPacket> {

    private final Collection<LivingBar> bars = new ArrayList<>();
    private final AtomicInteger nextEntityId = new AtomicInteger(90000);
    private volatile Task runningTask;

    @Override
    public void read(MinecraftBossBarPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {

    }

    @Override
    public void write(MinecraftBossBarPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        if(!(connection instanceof PendingConnection)) throw new UnsupportedOperationException("LivingBar legacy bridge is only supported for PendingConnections");
        LivingBar bar = Iterators.findOne(this.bars, bar1 -> bar1.barId == packet.getBarId() && bar1.owner.equals(connection));
        if(packet.getAction() == MinecraftBossBarPacket.Action.ADD){
            buffer.resetWriterIndex();
            buffer.resetReaderIndex();
            if(bar == null){
                bar = new LivingBar();
                bar.barId = packet.getBarId();
                bar.entityId = nextEntityId.getAndIncrement();
                bar.owner = (PendingConnection) connection;
                bars.add(bar);
                writeSpawnPacket(buffer,packet,bar.entityId);
                startScheduler();
            }else{
                writeUpdatePacket(buffer,packet,bar.entityId);
            }
        }else if(packet.getAction() == MinecraftBossBarPacket.Action.REMOVE){
            if(bar == null) return;
            this.bars.remove(bar);
            stopScheduler();
            writeDestroyPacket(buffer,bar);
        }else throw new UnsupportedOperationException("Other actions are not supported for legacy clients");
    }

    private void startScheduler(){
        if(runningTask != null) return;
        runningTask = McNative.getInstance().getScheduler().createTask(ObjectOwner.SYSTEM).interval(700, TimeUnit.MILLISECONDS).execute(() -> {
            Iterator<LivingBar> iterator = this.bars.iterator();
            while (iterator.hasNext()){
                LivingBar bar = iterator.next();
                if(bar.owner.isConnected()) sendMoveInformation(bar);
                else iterator.remove();
            }
            stopScheduler();
        });
    }

    private void stopScheduler(){
        if(runningTask == null || !this.bars.isEmpty()) return;
        runningTask.stop();
        runningTask = null;
    }

    private void writeSpawnPacket(ByteBuf buffer,MinecraftBossBarPacket packet,int entityId){
        buffer.writeByte(0x0F);//Reset packet id

        MinecraftProtocolUtil.writeVarInt(buffer,entityId);
        buffer.writeByte(64);
        buffer.writeInt(0);
        buffer.writeInt(5);
        buffer.writeInt(0);
        buffer.writeByte(256);
        buffer.writeByte(256);
        buffer.writeByte(256);
        buffer.writeShort(0);
        buffer.writeShort(0);
        buffer.writeShort(0);

        //Metadata
        writeMetadata(buffer,packet);
    }

    private void writeUpdatePacket(ByteBuf buffer,MinecraftBossBarPacket packet,int entityId){
        buffer.writeByte(0x1C);//Reset packet id
        MinecraftProtocolUtil.writeVarInt(buffer,entityId);
        writeMetadata(buffer,packet);
    }

    private void writeDestroyPacket(ByteBuf buffer,LivingBar bar){
        buffer.writeByte(0x13);
        MinecraftProtocolUtil.writeVarInt(buffer,1);
        MinecraftProtocolUtil.writeVarInt(buffer,bar.entityId);
    }

    private void writeMetadata(ByteBuf buffer,MinecraftBossBarPacket packet){
        writeMetaPrefix(buffer,17,2);
        buffer.writeInt(0);

        writeMetaPrefix(buffer,18,2);
        buffer.writeInt(0);

        writeMetaPrefix(buffer,19,2);
        buffer.writeInt(0);

        writeMetaPrefix(buffer,20,2);
        buffer.writeInt(1000);

        writeMetaPrefix(buffer,0,0);
        buffer.writeByte((byte) (1 << 5));

        //Health
        writeMetaPrefix(buffer,6,3);
        buffer.writeFloat(packet.getHealth()*300);

        VariableSet variables = packet.getTitleVariables() != null ?packet.getTitleVariables() : VariableSet.createEmpty();
        String legacyText = packet.getTitle().compileToString(MinecraftProtocolVersion.JE_1_7,variables);

        //Name
        writeMetaPrefix(buffer,10,4);
        MinecraftProtocolUtil.writeString(buffer,legacyText);

        //Name 2
        writeMetaPrefix(buffer,2,4);
        MinecraftProtocolUtil.writeString(buffer,legacyText);

        //Name Visible
        writeMetaPrefix(buffer,11,0);
        buffer.writeByte(1);

        //Name Visible 2
        writeMetaPrefix(buffer,11,0);
        buffer.writeByte(1);

        //End
        buffer.writeByte(127);
    }

    private void writeMetaPrefix(ByteBuf buffer,int index,int type){
        int i = (type << 5 | index & 0x1F) & 0xFF;
        buffer.writeByte(i);
    }

    private void sendMoveInformation(LivingBar bar){//@Todo Optimize with render distance
        Position position = bar.owner.getPlayer().getPosition();
        Vector vector = position.getDirection().multiply(32).add(position);

        ByteBuf buffer = Unpooled.directBuffer();
        buffer.writeByte(0x18);
        MinecraftProtocolUtil.writeVarInt(buffer,bar.entityId);
        buffer.writeInt(vector.getBlockX() * 32);
        buffer.writeInt(vector.getBlockY() * 32);
        buffer.writeInt(vector.getBlockZ() * 32);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeBoolean(false);
        bar.owner.sendRawPacket(buffer);
    }

    private static class LivingBar {

        private UUID barId;
        private int entityId;
        private PendingConnection owner;

    }
}
