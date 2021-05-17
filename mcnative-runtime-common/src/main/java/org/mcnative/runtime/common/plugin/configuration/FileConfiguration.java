/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.12.19, 20:36
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

package org.mcnative.runtime.common.plugin.configuration;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.WrappedDocument;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.io.IORuntimeException;
import org.mcnative.runtime.api.plugin.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileConfiguration extends WrappedDocument implements Configuration {

    public static DocumentFileType FILE_TYPE = DocumentFileType.YAML;

    private final ObjectOwner owner;
    private final String name;
    private final File file;

    public FileConfiguration(ObjectOwner owner, String name, File file) {
        this.owner = owner;
        this.name = name;
        this.file = file;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObjectOwner getOwner() {
        return owner;
    }

    @Override
    public boolean isFirstCreation() {
        return !file.exists();
    }

    @Override
    public void save() {
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        FILE_TYPE.getWriter().write(file, StandardCharsets.UTF_8,getOriginal(),true);
    }

    public void load(){
        if(!file.exists()) setOriginal(Document.newDocument());
        else {
            Document document = FILE_TYPE.getReader().read(file, StandardCharsets.UTF_8);
            setOriginal(document);
        }
    }

    @Override
    public void load(Class<?> configurationClass) {
        if(getOriginal() == null) load();
        Document.loadConfigurationClass(configurationClass,getOriginal());
        save();
    }
}
