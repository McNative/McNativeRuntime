/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 22.03.20, 12:15
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

package org.mcnative.runtime.common.serviceprovider.message.builder;

import net.pretronic.libraries.message.bml.builder.BasicMessageBuilder;
import net.pretronic.libraries.message.bml.builder.BuildContext;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.serviceprovider.placeholder.PlaceholderProvider;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;

public class PlaceholderVariableBuilder implements BasicMessageBuilder {

    private final static String VARIABLE_NOT_FOUND = "{PLACEHOLDER NOT FOUND}";

    @Override
    public Object build(BuildContext context,boolean requiresString, Object[] parameters, Object next) {
        PlaceholderProvider provider = McNative.getInstance().getRegistry().getServiceOrDefault(PlaceholderProvider.class);
        String result = VARIABLE_NOT_FOUND;

        if(provider != null && parameters.length == 1){
            if(context instanceof MinecraftTextBuildContext){
                MinecraftTextBuildContext minecraftContext = (MinecraftTextBuildContext) context;
                if(minecraftContext.getPlayer() != null){
                    String result0 = provider.translate(minecraftContext.getPlayer(),(String)parameters[0]);
                    if(result0 != null) result = result0;
                }
            }
        }

        if(next != null) return new Object[]{result,next};
        else return new Object[]{result};
    }

    @Override
    public boolean isUnformattedResultRequired() {
        return true;
    }
}
