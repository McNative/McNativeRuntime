package org.mcnative.runtime.common.event.player;

import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.event.player.design.MinecraftPlayerDesignRequestEvent;
import org.mcnative.runtime.api.event.player.design.MinecraftPlayerDesignSetEvent;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.PlayerDesign;

public class DefaultMinecraftPlayerDesignRequestEvent implements MinecraftPlayerDesignRequestEvent {

    private final MinecraftPlayer player;
    private PlayerDesign design;

    public DefaultMinecraftPlayerDesignRequestEvent(MinecraftPlayer player, PlayerDesign design) {
        this.player = player;
        this.design = design;
    }

    @Override
    public PlayerDesign getDesign() {
        return design;
    }

    @Override
    public void setDesign(PlayerDesign playerDesign) {
        Validate.notNull(playerDesign);
        this.design = playerDesign;
    }

    @Override
    public MinecraftPlayer getPlayer() {
        return player;
    }
}
