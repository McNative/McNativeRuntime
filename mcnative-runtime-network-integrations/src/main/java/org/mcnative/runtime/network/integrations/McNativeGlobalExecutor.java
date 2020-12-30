/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.07.20, 11:23
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

package org.mcnative.runtime.network.integrations;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.protocol.MinecraftEdition;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.text.components.MessageComponent;

public class McNativeGlobalExecutor {

    public static void broadcast(MessageComponent<?> component, VariableSet variables) {
        execute(Document.newDocument()
                .set("action","broadcast")
                .set("message",component.compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),variables)));
    }

    public static void broadcast(String permission, MessageComponent<?> component, VariableSet variables) {
        execute(Document.newDocument()
                .set("action","broadcast")
                .set("permission",permission)
                .set("message",component.compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),variables)));
    }

    public static void kickAll(MessageComponent<?> component, VariableSet variables) {
        execute(Document.newDocument()
                .set("action","kickAll")
                .set("message",component.compile(MinecraftProtocolVersion.getLatest(MinecraftEdition.JAVA),variables)));
    }

    private static void execute(Document data){
        McNative.getInstance().getNetwork().getMessenger()
                .sendMessage(NetworkIdentifier.BROADCAST_PROXY,"mcnative_global",data);
    }
}
