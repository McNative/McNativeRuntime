/*
 * (C) Copyright 2020 The PretronicLibraries Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.04.20, 19:48
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

package org.mcnative.runtime.common.serviceprovider.message.functions;

import net.pretronic.libraries.message.bml.Module;
import net.pretronic.libraries.message.bml.builder.BuildContext;
import net.pretronic.libraries.message.bml.function.Function;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;

public class IFHasPermissionFunction implements Function {

    @Override
    public Object execute(BuildContext context, Module leftOperator0, String operation, Module rightOperation0, Module[] parameters) {
        if(parameters.length < 2){
            throw new IllegalArgumentException("IFHasPermissionFunction needs at least two parameter");
        }

        boolean ok = false;
        if(context instanceof MinecraftTextBuildContext){
            ok = ((MinecraftTextBuildContext) context).getSender().hasPermission(parameters[0].toString());
        }

        if(ok){
            return parameters[1].build(context,false);
        }else if(parameters.length > 2){
            return parameters[2].build(context,false);
        }else return "";
    }
}
