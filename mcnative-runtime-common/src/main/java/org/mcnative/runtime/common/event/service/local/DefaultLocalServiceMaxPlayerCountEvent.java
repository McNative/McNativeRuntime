package org.mcnative.runtime.common.event.service.local;

import org.mcnative.runtime.api.event.service.local.LocalServiceMaxPlayerCountEvent;

public class DefaultLocalServiceMaxPlayerCountEvent implements LocalServiceMaxPlayerCountEvent {

    private int maxPlayers;

    public DefaultLocalServiceMaxPlayerCountEvent(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public int getMaxPlayerCount() {
        return maxPlayers;
    }

    @Override
    public void setMaxPlayerCount(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
