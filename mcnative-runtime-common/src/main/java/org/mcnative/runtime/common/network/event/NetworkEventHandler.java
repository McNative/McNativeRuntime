/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 31.07.20, 14:40
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

import net.pretronic.libraries.event.DefaultEventBus;
import net.pretronic.libraries.event.network.EventOrigin;
import net.pretronic.libraries.event.network.NetworkEvent;
import org.mcnative.runtime.api.McNative;

public class NetworkEventHandler extends DefaultEventBus.NetworkEventHandler {

    @Override
    public EventOrigin getLocal() {
        return McNative.getInstance().getLocal();
    }

    @Override
    public void handleNetworkEventsAsync(EventOrigin origin0, Class<?> executionClass, Object[] events) {
        System.out.println("[DEBUG] Executing network event "+executionClass);
        if(!McNative.getInstance().isNetworkAvailable()){
            NetworkEvent info = executionClass.getAnnotation(NetworkEvent.class);
            if(info.ignoreNetworkException()) return;
        }
        EventOrigin origin = origin0 != null ? origin0  : getLocal();
        McNative.getInstance().getNetwork().getEventBus().callEventsAsync(origin,executionClass,events);
    }
}
