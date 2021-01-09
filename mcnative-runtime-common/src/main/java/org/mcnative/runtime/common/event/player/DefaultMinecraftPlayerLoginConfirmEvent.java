package org.mcnative.runtime.common.event.player;

import org.mcnative.runtime.api.event.player.login.MinecraftPlayerLoginConfirmEvent;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

public class DefaultMinecraftPlayerLoginConfirmEvent implements MinecraftPlayerLoginConfirmEvent {

    private final OnlineMinecraftPlayer player;

    public DefaultMinecraftPlayerLoginConfirmEvent(OnlineMinecraftPlayer player) {
        this.player = player;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer() {
        return this.player;
    }

    @Override
    public MinecraftPlayer getPlayer() {
        return getOnlinePlayer();
    }
}
