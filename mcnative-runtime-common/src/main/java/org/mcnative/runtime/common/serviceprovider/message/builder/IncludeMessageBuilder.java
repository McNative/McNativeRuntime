/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 27.03.20, 22:04
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

import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.message.bml.Message;
import net.pretronic.libraries.message.bml.builder.BasicMessageBuilder;
import net.pretronic.libraries.message.bml.builder.BuildContext;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
import org.mcnative.runtime.api.text.context.TextBuildType;

public class IncludeMessageBuilder implements BasicMessageBuilder {

    private final MessageProvider provider;
    private Message message;

    public IncludeMessageBuilder(MessageProvider provider) {
        this.provider = provider;
    }

    @Override
    public Object build(BuildContext context, boolean requiresUnformatted, Object[] parameters, Object next) {
        if(message == null){
            message = provider.getMessage((String)parameters[0],context.getLanguage());
            if(message == null) message = Message.ofStaticText("{MESSAGE NOT FOUND}");
        }
        return buildMessage(context,message,requiresUnformatted,next);
    }

    public static Object buildMessage(BuildContext context,Message message, boolean requiresUnformatted, Object next){
        Object result = message.build(context);
        if(requiresUnformatted){
            return TextBuildUtil.buildUnformattedText(result,next);
        }else{
            if (context instanceof MinecraftTextBuildContext) {
                MinecraftTextBuildContext minecraftContext = context.getAs(MinecraftTextBuildContext.class);
                if(minecraftContext.getType() == TextBuildType.COMPILE){
                    if (next != null) return new Object[]{result, next};
                    else return new Object[]{result};
                }else if(minecraftContext.getType() == TextBuildType.COMPILE_RAW){
                    return TextBuildUtil.buildCompileTextRaw((MinecraftTextBuildContext) context,result, next);
                }else if(minecraftContext.getType() == TextBuildType.LEGACY){
                    return TextBuildUtil.buildLegacyText(result, next);
                }
            }
            return TextBuildUtil.buildPlainText(result,next);
        }
    }

    @Override
    public boolean isUnformattedResultRequired() {
        return true;
    }
}
