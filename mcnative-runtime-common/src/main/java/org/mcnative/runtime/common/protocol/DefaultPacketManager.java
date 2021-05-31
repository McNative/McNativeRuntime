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

import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.runtime.api.connection.ConnectionState;
import org.mcnative.runtime.api.protocol.Endpoint;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.protocol.packet.*;

import java.util.*;
import java.util.function.Function;

/**
 * A default implementation of the packet manager
 */
public class DefaultPacketManager implements PacketManager {

    private static final Function<Class<?>, List<MinecraftPacketListener>> COMPUTE_FUNCTION = class0 -> new ArrayList<>();

    private final Collection<PacketRegistration> registrations;
    private final Map<Class<?>, List<MinecraftPacketListener>> upstreamOutgoingListeners;
    private final Map<Class<?>, List<MinecraftPacketListener>> upstreamIncomingListeners;
    private final Map<Class<?>, List<MinecraftPacketListener>> downstreamOutgoingListeners;
    private final Map<Class<?>, List<MinecraftPacketListener>> downstreamIncomingListeners;

    public DefaultPacketManager() {
        this.registrations = new ArrayList<>();
        this.upstreamOutgoingListeners = new HashMap<>();
        this.upstreamIncomingListeners = new HashMap<>();
        this.downstreamOutgoingListeners = new HashMap<>();
        this.downstreamIncomingListeners = new HashMap<>();
    }

    @Override
    public Collection<PacketRegistration> getPacketRegistrations() {
        return this.registrations;
    }

    @Override
    public PacketRegistration getPacketRegistration(Class<?> packetClass) {
        PacketRegistration identifier = Iterators.findOne(registrations, identifier0 -> identifier0.getPacketClass().equals(packetClass));
        if(identifier == null) throw new IllegalArgumentException("No packet registration for "+packetClass+" found.");
        return identifier;
    }

    @Override
    public PacketRegistration getPacketRegistration(ConnectionState state, PacketDirection direction, MinecraftProtocolVersion version, int packetId) {
        for (PacketRegistration registration : registrations) {
            PacketRegistration.PacketCondition condition = registration.getCondition(direction, state);
            if(condition != null){
                for (int i = condition.getMappings().length - 1; i >= 0; i--) {
                    PacketRegistration.IdMapping mapping = condition.getMappings()[i];
                    if(version.isNewerOrSame(mapping.getVersion())){
                        if(mapping.getId() == packetId){
                            return registration;
                        }
                        break;
                    }
                }
            }
        }
        return null;//Missing registrations are currently allowed and should be handled
    }

    @Override
    public void registerPacket(PacketRegistration registration) {
        Validate.notNull(registration);
        this.registrations.add(registration);
    }

    @Override
    public Pair<PacketRegistration, MinecraftPacketCodec<?>> getPacketCodecData(ConnectionState state, PacketDirection direction, MinecraftProtocolVersion version, int packetId) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MinecraftPacket> List<MinecraftPacketListener> getPacketListeners(Endpoint endpoint, PacketDirection direction, Class<T> packetClass) {
        Map<Class<?>, List<MinecraftPacketListener>> listeners =  getListenerMap(endpoint, direction);
        List<?> listeners0 = listeners.get(packetClass);
        if(listeners0 == null) return null;
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
