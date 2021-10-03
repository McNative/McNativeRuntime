/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 22.03.20, 12:25
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
import net.pretronic.libraries.message.bml.builder.MessageBuilder;
import net.pretronic.libraries.message.bml.builder.MessageBuilderFactory;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
import org.mcnative.runtime.api.text.context.TextBuildType;
import org.mcnative.runtime.api.text.format.ColoredString;

public class TextBuilder implements BasicMessageBuilder {

    private final ColoredString input;

    public TextBuilder(String input) {
        this.input = new ColoredString(input.replace("\\n","\n"));
    }

    @Override
    public Object build(BuildContext context,boolean requiresUnformatted, Object[] parameters,Object next) {
        if(requiresUnformatted){
            return TextBuildUtil.buildUnformattedText(input,next);
        }else{
            if(context instanceof MinecraftTextBuildContext){
                MinecraftTextBuildContext minecraftContext = context.getAs(MinecraftTextBuildContext.class);
                if(minecraftContext.getType() == TextBuildType.COMPILE){
                    return TextBuildUtil.buildCompileText(minecraftContext,input,next);
                }else if(minecraftContext.getType() == TextBuildType.COMPILE){
                    return TextBuildUtil.buildCompileTextRaw(minecraftContext,input,next);
                }else if(minecraftContext.getType() == TextBuildType.LEGACY){
                    return TextBuildUtil.buildLegacyText(input,next);
                }
            }
            return TextBuildUtil.buildPlainText(input,next);
        }
    }

    @Override
    public boolean isUnformattedResultRequired() {
        return false;
    }

    public static class Factory implements MessageBuilderFactory {

        @Override
        public MessageBuilder create(String name) {
            return new TextBuilder(name);
        }
    }
}
