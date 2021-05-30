package org.mcnative.runtime.common.event.player.complete;

import org.mcnative.runtime.api.event.player.MinecraftPlayerTabCompleteResponseEvent;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.protocol.packet.type.player.complete.MinecraftPlayerTabCompleteResponsePacket;

import java.util.List;

public class DefaultMinecraftPlayerTabCompleteResponseEvent implements MinecraftPlayerTabCompleteResponseEvent {

    private final MinecraftPlayerTabCompleteResponsePacket packet;

    private final String cursor;
    private final OnlineMinecraftPlayer player;
    private boolean cancelled;

    public DefaultMinecraftPlayerTabCompleteResponseEvent(MinecraftPlayerTabCompleteResponsePacket packet,String cursor, OnlineMinecraftPlayer player) {
        this.packet = packet;
        this.cursor = cursor;
        this.player = player;
        this.cancelled = false;
    }

    @Override
    public String getCursor() {
        return cursor;
    }

    @Override
    public List<String> getSuggestions() {
        return packet.getSuggestions();
    }

    @Override
    public void setSuggestions(List<String> list) {
        packet.setSuggestions(list);
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
