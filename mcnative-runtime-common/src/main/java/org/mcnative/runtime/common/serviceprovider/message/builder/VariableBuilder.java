/*
 * (C) Copyright 2020 The PretronicLibraries Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.03.20, 17:04
 * @web %web%
 *
 * The PretronicLibraries Project is under the Apache License, version 2.0 (the "License");
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

import net.pretronic.libraries.message.bml.Message;
import net.pretronic.libraries.message.bml.builder.BasicMessageBuilder;
import net.pretronic.libraries.message.bml.builder.BuildContext;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
import org.mcnative.runtime.api.text.context.TextBuildType;

public class VariableBuilder implements BasicMessageBuilder {

    public VariableBuilder(){ }

    @Override
    public Object build(BuildContext context, boolean requiresUnformatted,Object[] parameters, Object next) {
        Object value = parameters.length > 0 ? context.getVariables().getValue((String) parameters[0]) : "[VAR NOT FOUND]";

        if(requiresUnformatted){
            return TextBuildUtil.buildUnformattedText(value,next);
        }

        if(value instanceof Message){
            return IncludeMessageBuilder.BuildMessage(context, (Message) value,requiresUnformatted,next);
        }

        if(context instanceof MinecraftTextBuildContext){
            MinecraftTextBuildContext minecraftContext = context.getAs(MinecraftTextBuildContext.class);
            if(minecraftContext.getType() == TextBuildType.COMPILE){
                return TextBuildUtil.buildCompileText(minecraftContext,value,next);
            }else if(minecraftContext.getType() == TextBuildType.LEGACY){
                return TextBuildUtil.buildLegacyText(value,next);
            }
        }
        return TextBuildUtil.buildPlainText(value,next);
    }

    @Override
    public boolean isUnformattedResultRequired() {
        return true;
    }
}
