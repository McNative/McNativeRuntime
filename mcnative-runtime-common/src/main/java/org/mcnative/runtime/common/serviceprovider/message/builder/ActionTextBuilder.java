/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 22.03.20, 12:23
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

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.Module;
import net.pretronic.libraries.message.bml.builder.BuildContext;
import net.pretronic.libraries.message.bml.builder.MessageBuilder;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
import org.mcnative.runtime.api.text.context.TextBuildType;
import org.mcnative.runtime.api.text.event.HoverAction;
import org.mcnative.runtime.api.text.format.ColoredString;

public class ActionTextBuilder implements MessageBuilder {

    @Override
    public Object build(BuildContext context, boolean requiresUnformatted, String name, Module leftOperator0, String operation, Module rightOperator0, Module[] parameters0, Module extension0, Module next0) {
        if(requiresUnformatted){
            throw new IllegalArgumentException("Action component can not be used in inner context");
        }

        Object[] parameters = new Object[parameters0.length];
        int index = 0;
        for (Module parameter : parameters0) {
            parameters[index] = parameter.build(context,false);
            index++;
        }

        Object next = null;
        if (next0 != null) next = next0.build(context, false);
        Object extension = this.buildModule(extension0, context, true);

        if(context instanceof MinecraftTextBuildContext){
            MinecraftTextBuildContext minecraftContext = (MinecraftTextBuildContext) context;
            if(minecraftContext.getType() == TextBuildType.COMPILE){
                return buildCompileActionText(minecraftContext,parameters, next, extension);
            }
        }

        StringBuilder builder = new StringBuilder();
        String text = parameters[0].toString();
        builder.append(TextBuildUtil.buildLegacyText(new ColoredString(text),null));

        if(extension != null && !text.equals(extension)){
            builder.append('(');
            builder.append(extension);
            builder.append(')');
        }
        if(next != null){
            builder.append(next);
        }

        return builder.toString();
    }

    private Object buildCompileActionText(MinecraftTextBuildContext context, Object[] parameters, Object next, Object extension) {
        Document result = Document.newDocument();
        result.add("text","");

        if(parameters.length >= 2){
            Document hover = Document.newDocument();
            hover.set("action", HoverAction.SHOW_TEXT.getName());
            hover.set("value",parameters[1]);
            result.add("hoverEvent",hover);
        }

        if(extension != null){
            Document click = Document.newDocument();
            String value = extension.toString();
            boolean command = false;
            if(value.startsWith("run://")){
                command = true;
                value = value.substring(6);
            }
            click.set("action",command ? "run_command" : "open_url");
            click.set("value",value);
            result.add("clickEvent",click);
        }
        result.add("extra",new Object[]{parameters[0]});
        if(next != null) return new Object[]{result,next};
        else return new Object[]{result};
    }
}
