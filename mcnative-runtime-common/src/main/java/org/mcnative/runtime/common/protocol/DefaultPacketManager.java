/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.10.19, 11:26
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

package org.mcnative.runtime.common.protocol;

import org.mcnative.runtime.api.protocol.Endpoint;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacket;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketListener;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.PacketManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A default implementation of the packet manager
 */
public class DefaultPacketManager implements PacketManager {

    private static final Function<Class<?>, List<MinecraftPacketListener>> COMPUTE_FUNCTION = class0 -> new ArrayList<>();

    private final Map<Class<?>, List<MinecraftPacketListener>> upstreamOutgoingListeners;
    private final Map<Class<?>, List<MinecraftPacketListener>> upstreamIncomingListeners;
    private final Map<Class<?>, List<MinecraftPacketListener>> downstreamOutgoingListeners;
    private final Map<Class<?>, List<MinecraftPacketListener>> downstreamIncomingListeners;

    public DefaultPacketManager() {
        this.upstreamOutgoingListeners = new HashMap<>();
        this.upstreamIncomingListeners = new HashMap<>();
        this.downstreamOutgoingListeners = new HashMap<>();
        this.downstreamIncomingListeners = new HashMap<>();
    }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends MinecraftPacket> List<MinecraftPacketListener> getPacketListeners(Endpoint endpoint, PacketDirection direction, Class<T> packetClass) {
        Map<Class<?>, List<MinecraftPacketListener>> listeners =  getListenerMap(endpoint, direction);
        List<?> listeners0 = listeners.get(packetClass);
        List<MinecraftPacketListener> result = new ArrayList<>();
        for (Object o : listeners0) result.add((MinecraftPacketListener) o);
        return result;
    }

    @Override
    public <T extends MinecraftPacket> void registerPacketListener(Endpoint endpoint, PacketDirection direction, Class<T> packetClass, MinecraftPacketListener listener) {
        Map<Class<?>, List<MinecraftPacketListener>> listeners =  getListenerMap(endpoint, direction);
        listeners.computeIfAbsent(packetClass,COMPUTE_FUNCTION).add(listener);
    }

    @Override
    public void unregisterPacketListener(MinecraftPacketListener minecraftPacketListener) {
        throw new UnsupportedOperationException();
    }

    private Map<Class<?>, List<MinecraftPacketListener>> getListenerMap(Endpoint endpoint, PacketDirection direction){
        if(endpoint == Endpoint.UPSTREAM){
            return direction == PacketDirection.OUTGOING ? upstreamOutgoingListeners : upstreamIncomingListeners;
        }else{
            return direction == PacketDirection.OUTGOING ? downstreamOutgoingListeners : downstreamIncomingListeners;
        }
    }

}
