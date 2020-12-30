/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 06.04.20, 22:56
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

package org.mcnative.runtime.common.serviceprovider.message;

import net.pretronic.libraries.message.MessagePack;
import net.pretronic.libraries.message.MessagePackMeta;

import java.io.File;
import java.util.LinkedHashMap;

public class FileMessagePack extends MessagePack {

    private File file;

    public FileMessagePack(MessagePackMeta meta, File file) {
        super(meta);
        this.file = file;
    }

    public FileMessagePack(MessagePackMeta meta, LinkedHashMap<String, String> messages, File file) {
        super(meta, messages);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
