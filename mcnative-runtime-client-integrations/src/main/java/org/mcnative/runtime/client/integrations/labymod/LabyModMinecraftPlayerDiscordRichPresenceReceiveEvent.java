package org.mcnative.runtime.client.integrations.labymod;

import org.mcnative.runtime.api.event.player.MinecraftPlayerDiscordRichPresenceReceiveEvent;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

public class LabyModMinecraftPlayerDiscordRichPresenceReceiveEvent implements MinecraftPlayerDiscordRichPresenceReceiveEvent {

    private final ConnectedMinecraftPlayer player;
    private final String spectateSecret;
    private final String joinSecret;

    public LabyModMinecraftPlayerDiscordRichPresenceReceiveEvent(ConnectedMinecraftPlayer player, String spectateSecret, String joinSecret) {
        this.player = player;
        this.spectateSecret = spectateSecret;
        this.joinSecret = joinSecret;
    }

    @Override
    public String getSpectateSecret() {
        return spectateSecret;
    }

    @Override
    public String getJoinSecret() {
        return joinSecret;
    }

    @Override
    public OnlineMinecraftPlayer getOnlinePlayer() {
        return player;
    }

    @Override
    public MinecraftPlayer getPlayer() {
        return player;
    }
}
