/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.03.20, 17:57
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

package org.mcnative.runtime.common.player;

import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.player.PlayerDesign;

public class DefaultPlayerDesign implements PlayerDesign {

    private final String color;
    private final String prefix;
    private final String suffix;
    private final String chat;
    private final int priority;

    public DefaultPlayerDesign(String color, String prefix, String suffix, String chat, int priority) {
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.chat = chat;
        this.priority = priority;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getChat() {
        return chat;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public static PlayerDesign fromJson(String json){
        Validate.notNull(json);
        if(json.equals("{}")) return null;
        else return DocumentFileType.JSON.getReader().read(json).getAsObject(DefaultPlayerDesign.class);
    }
}
