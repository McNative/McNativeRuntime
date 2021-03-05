package org.mcnative.runtime.common.maf;

import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.actionframework.sdk.actions.server.ServerRecoveryAction;
import org.mcnative.actionframework.sdk.client.MAFClient;
import org.mcnative.actionframework.sdk.client.StatusListener;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MAFStatusListener implements StatusListener {

    private MAFClient client;
    private boolean recovery;

    public MAFStatusListener() {
        this.recovery = false;
    }

    @Override
    public void onConnect() {
        McNative.getInstance().getLocal().getEventBus().subscribe(ObjectOwner.SYSTEM,new MAFListener(client));
        if(recovery) {
            ServerRecoveryAction recoveryAction = new ServerRecoveryAction(getOnlinePlayers());
            this.client.sendAction(recoveryAction);
            this.recovery = false;
        }
        if(MAFListener.STARTUP){
            MAFUtil.sendStartupAction(client);
            McNative.getInstance().getScheduler().createTask(ObjectOwner.SYSTEM).async()
                    .delay(10, TimeUnit.SECONDS)
                    .execute(() -> MAFUtil.sendInfoAction(client));
        }
    }

    @Override
    public void onDisconnect() {
        McNative.getInstance().getLocal().getEventBus().unsubscribeAll(MAFListener.class);
        if(client != null) this.recovery = true;
    }

    private Map<UUID, Integer> getOnlinePlayers() {
        Map<UUID, Integer> players = new HashMap<>();
        for (ConnectedMinecraftPlayer onlinePlayer : McNative.getInstance().getLocal().getConnectedPlayers()) {
            players.put(onlinePlayer.getUniqueId(), onlinePlayer.getProtocolVersion().getNumber());
        }
        return players;
    }

    protected void setClient(MAFClient client) {
        this.client = client;
    }
}
