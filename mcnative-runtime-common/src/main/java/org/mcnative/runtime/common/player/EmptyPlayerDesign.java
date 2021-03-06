package org.mcnative.runtime.common.player;

import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import org.mcnative.runtime.api.player.PlayerDesign;

public class EmptyPlayerDesign implements PlayerDesign {

    public static final EmptyPlayerDesign DEFAULT = new EmptyPlayerDesign();

    private EmptyPlayerDesign () {
        VariableDescriberRegistry.registerDescriber(EmptyPlayerDesign.class);
    }

    @Override
    public String getColor() {
        return "";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public String getChat() {
        return "";
    }

    @Override
    public int getPriority() {
        return 0;
    }
}