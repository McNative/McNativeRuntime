package org.mcnative.runtime.common.serviceprovider;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.message.bml.Message;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
import org.mcnative.runtime.api.text.context.TextBuildType;
import org.mcnative.runtime.common.serviceprovider.message.DefaultMessageProvider;

public class Test {

    public static void main(String[] args) {
        DefaultMessageProvider provider = new DefaultMessageProvider();

        Message m = provider.getProcessor().parse("&b{test} &eTest");

        VariableSet variables = VariableSet.create();
        variables.add("test","Hallo");

        Document result = (Document) m.build(new MinecraftTextBuildContext(null,variables, MinecraftProtocolVersion.JE_1_8,null, TextBuildType.COMPILE));

        System.out.println(DocumentFileType.JSON.getWriter().write(result,true));
    }
}
