package org.mcnative.runtime.common.event.player.complete;

import org.mcnative.runtime.api.event.player.MinecraftPlayerTabCompleteEvent;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.protocol.packet.type.player.complete.MinecraftPlayerTabCompletePacket;

public class DefaultMinecraftPlayerTabCompleteEvent implements MinecraftPlayerTabCompleteEvent {

    private final MinecraftPlayerTabCompletePacket packet;
    private final OnlineMinecraftPlayer player;
    private boolean cancelled;

    public DefaultMinecraftPlayerTabCompleteEvent(MinecraftPlayerTabCompletePacket packet, OnlineMinecraftPlayer player) {
        this.packet = packet;
        this.player = player;
        this.cancelled = false;
    }

    @Override
    public String getCursor() {
        return packet.getCursor();
    }

    @Override
    public void setCursor(String cursor) {
        packet.setCursor(cursor);
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
    public OnlineMinecraftPlayer getOnlinePlayer() {
        return player;
    }

    @Override
    public MinecraftPlayer getPlayer() {
        return player;
    }
}
