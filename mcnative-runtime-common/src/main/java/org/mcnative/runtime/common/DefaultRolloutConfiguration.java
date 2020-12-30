/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 25.07.20, 12:24
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

package org.mcnative.runtime.common;

import net.pretronic.libraries.document.type.DocumentFileType;
import org.mcnative.runtime.api.rollout.RolloutConfiguration;
import org.mcnative.runtime.api.rollout.RolloutProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultRolloutConfiguration implements RolloutConfiguration {

    private Map<String, RolloutProfile> profiles;
    private Collection<PluginEntry> plugins;

    public DefaultRolloutConfiguration() {
        profiles = new HashMap<>();
        plugins = new ArrayList<>();
    }

    public Map<String,RolloutProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, RolloutProfile> profiles) {
        this.profiles = profiles;
    }

    public Collection<PluginEntry> getPlugins() {
        return plugins;
    }

    public void setPlugins(Collection<PluginEntry> plugins) {
        this.plugins = plugins;
    }

    public RolloutProfile getProfile(String name){
        for (PluginEntry plugin : plugins) {
            if(plugin.getPluginName().equalsIgnoreCase(name)){
                RolloutProfile profile = profiles.get(plugin.getProfile());
                if(profile != null) return profile;
                break;
            }
        }
        return RolloutProfile.DEFAULT;
    }

    public static DefaultRolloutConfiguration load(File file) {
        if(file.exists()) return DocumentFileType.YAML.getReader().read(file).getAsObject(DefaultRolloutConfiguration.class);
        else return new DefaultRolloutConfiguration();
    }
}
