/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.06.20, 13:35
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

package org.mcnative.runtime.common.player.data;

import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.PlayerSetting;
import org.mcnative.runtime.api.player.data.PlayerDataProvider;
import org.mcnative.runtime.common.plugin.configuration.DefaultSetting;

public class DefaultPlayerSetting extends DefaultSetting implements PlayerSetting {

    public DefaultPlayerSetting(int id, String owner, String key, Object value, long created, long updated) {
        super(id, owner, key, value, created, updated);
    }

    @Override
    public void setValue(Object value) {
        Validate.notNull(value);
        this.value = value;
        McNative.getInstance().getRegistry().getService(PlayerDataProvider.class).updateSetting(this);
    }
}
