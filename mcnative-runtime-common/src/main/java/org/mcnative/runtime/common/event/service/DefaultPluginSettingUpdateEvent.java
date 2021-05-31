package org.mcnative.runtime.common.event.service;

import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.Setting;
import org.mcnative.runtime.api.event.service.PluginSettingUpdateEvent;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;

public class DefaultPluginSettingUpdateEvent implements PluginSettingUpdateEvent {

    private final String owner;
    private final String key;

    private transient Setting setting;

    public DefaultPluginSettingUpdateEvent(String owner, String key,Setting setting) {
        this.owner = owner;
        this.key = key;
        this.setting = setting;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Setting getSetting() {
        if(setting == null) setting = McNative.getInstance().getRegistry().getService(ConfigurationProvider.class).getSetting(owner,key);
        return setting;
    }

}
