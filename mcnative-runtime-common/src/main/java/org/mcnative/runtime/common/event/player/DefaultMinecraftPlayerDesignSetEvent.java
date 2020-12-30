package org.mcnative.runtime.common.event.player;

import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.event.player.design.MinecraftPlayerDesignSetEvent;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.PlayerDesign;

public class DefaultMinecraftPlayerDesignSetEvent implements MinecraftPlayerDesignSetEvent {

    private final MinecraftPlayer player;
    private boolean cancelled;
    private PlayerDesign design;

    public DefaultMinecraftPlayerDesignSetEvent(MinecraftPlayer player, PlayerDesign design) {
        this.player = player;
        this.design = design;
        cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
