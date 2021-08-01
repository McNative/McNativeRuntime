package org.mcnative.runtime.common.serviceprovider.message.functions;

import net.pretronic.libraries.message.bml.builder.BuildContext;
import net.pretronic.libraries.message.bml.function.ParametrizedFunction;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;

public class HasPermissionFunction implements ParametrizedFunction {

    @Override
    public Object execute(BuildContext context, Object[] parameters) {
        if(parameters.length != 1) throw new IllegalArgumentException("HasPermissionFunction required 1 parameter");

        if(context instanceof MinecraftTextBuildContext){
            return ((MinecraftTextBuildContext) context).getSender().hasPermission(parameters[0].toString());
        }

        return context;
    }
}
