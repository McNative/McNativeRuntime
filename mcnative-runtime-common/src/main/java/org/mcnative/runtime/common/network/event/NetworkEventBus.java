/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.05.20, 17:31
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

package org.mcnative.runtime.common.network.event;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.event.DefaultEventBus;
import net.pretronic.libraries.event.executor.MethodEventExecutor;
import net.pretronic.libraries.event.network.*;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.reflect.ReflectException;
import net.pretronic.libraries.utility.reflect.UnsafeInstanceCreator;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.messaging.MessageReceiver;
import org.mcnative.runtime.api.network.messaging.MessagingChannelListener;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

public class NetworkEventBus extends DefaultEventBus implements MessagingChannelListener {

    @Override
    public void subscribe(ObjectOwner owner, Object listener) {
        Objects.requireNonNull(owner, "Owner can't be null.");
        Objects.requireNonNull(listener, "Listener can't be null.");

        for (Method method : listener.getClass().getDeclaredMethods()) {
            try {
                NetworkListener info = method.getAnnotation(NetworkListener.class);
                if (info != null && (method.getParameterTypes().length == 1 || method.getParameterTypes().length == 2)) {
                    Class<?> eventClass = method.getParameterTypes()[0];
                    Class<?> mappedClass = getMappedClass(eventClass);
                    if (mappedClass == null) mappedClass = eventClass;
                    addExecutor(mappedClass, new MethodEventExecutor(owner, info.priority(),info.execution(), listener, eventClass, method,info.onlyRemote(),info.onlyLocal()));
                }
            } catch (Exception var11) {
                throw new IllegalArgumentException("Could not register listener " + listener, var11);
            }
        }
    }

    @Override
    public <T> void callEvents(EventOrigin origin,Class<T> executionClass, Object... events) {
        if(events.length > 1) throw new IllegalArgumentException("Network eventbus can not execute multiple events for the same execution class");
        NetworkEvent event = getNetworkEvent(executionClass);
        if(event.type() != NetworkEventType.SELF_MANAGED){
            callNetworkEvents(executionClass,events);
        }
        super.callEvents(origin,executionClass, events);
    }

    @Override
    public <T> void callEventsAsync(EventOrigin origin,Class<T> executionClass, Runnable callback, Object... events) {
        if(events.length > 1) throw new IllegalArgumentException("Network eventbus can not execute multiple events for the same execution class");
        NetworkEvent event = getNetworkEvent(executionClass);
        if(event.type() != NetworkEventType.SELF_MANAGED){
            callNetworkEvents(executionClass,events);
        }
        super.callEventsAsync(origin,executionClass,callback, events);
    }

    private <T> NetworkEvent getNetworkEvent(Class<T> executionClass){
        NetworkEvent event = executionClass.getAnnotation(NetworkEvent.class);
        if(event == null) throw new IllegalArgumentException(executionClass.getName()+" is not a @NetworkEvent");
        return event;
    }

    private <T> void callNetworkEvents(Class<T> executionClass,Object[] events){
        McNative.getInstance().getExecutorService().execute(() -> {
            Object event = events[0];

            Document eventData;
            if(event instanceof NetworkEventAdapter) {
                eventData = Document.newDocument();
                ((NetworkEventAdapter) event).write(eventData);
            }else eventData = Document.newDocument(event);

            eventData.set("EVENT_CLASS",event.getClass());

            if(executionClass != event.getClass()){
                eventData.set("EXECUTION_CLASS",executionClass);
            }

            McNative.getInstance().getNetwork().sendBroadcastMessage("mcnative_event",eventData);
        });
    }

    @Internal
    public void executeNetworkEvent(EventOrigin origin,Document data){
        try{
            Class<?> executionClass = data.getObject("EXECUTION_CLASS",Class.class);
            Class<?> eventClass = data.getObject("EVENT_CLASS",Class.class);
            if(executionClass == null) executionClass = eventClass;

            Object event;
            if(NetworkEventAdapter.class.isAssignableFrom(eventClass)){
                event = UnsafeInstanceCreator.newInstance(eventClass);
                ((NetworkEventAdapter)event).read(data);
            }else{
                event = data.getAsObject(eventClass);
            }
            super.callEventsAsync(origin,executionClass,null,event);
        }catch (ReflectException exception){
            if(!(exception.getCause() instanceof ClassNotFoundException)){
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Document onMessageReceive(MessageReceiver sender, UUID requestId, Document request) {
        executeNetworkEvent(sender,request);
        return null;
    }
}
