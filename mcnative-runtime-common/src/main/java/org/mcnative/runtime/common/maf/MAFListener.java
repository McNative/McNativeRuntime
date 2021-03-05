package org.mcnative.runtime.common.maf;

import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.utility.SystemInfo;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.actionframework.sdk.actions.player.PlayerJoinAction;
import org.mcnative.actionframework.sdk.actions.player.PlayerLeaveAction;
import org.mcnative.actionframework.sdk.actions.server.ServerInfoAction;
import org.mcnative.actionframework.sdk.actions.server.ServerShutdownAction;
import org.mcnative.actionframework.sdk.actions.server.ServerStartupAction;
import org.mcnative.actionframework.sdk.actions.server.ServerStatusAction;
import org.mcnative.actionframework.sdk.client.MAFClient;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.MinecraftPlatform;
import org.mcnative.runtime.api.ServerPerformance;
import org.mcnative.runtime.api.event.player.MinecraftPlayerLogoutEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerLoginConfirmEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.runtime.api.event.service.local.LocalServiceReloadEvent;
import org.mcnative.runtime.api.event.service.local.LocalServiceShutdownEvent;
import org.mcnative.runtime.api.event.service.local.LocalServiceStartupEvent;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class MAFListener {

    protected static boolean STARTUP = false;
    private final MAFClient client;

    public MAFListener(MAFClient client) {
        this.client = client;
    }

    /* Local Service */

    @Listener(priority = EventPriority.HIGHEST)
    public void onStartup(LocalServiceStartupEvent event){
        STARTUP = true;
        MAFUtil.sendStartupAction(client);
        McNative.getInstance().getScheduler().createTask(ObjectOwner.SYSTEM).async()
                .delay(15, TimeUnit.SECONDS)
                .execute(() -> MAFUtil.sendInfoAction(client));
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onStartup(LocalServiceReloadEvent event){
        MAFUtil.sendInfoAction(client);
        MAFUtil.sendStatusAction(client);
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onShutdown(LocalServiceShutdownEvent event){
        if(!client.getConnection().isConnected()) return;
        client.sendAction(new ServerShutdownAction());
    }

    /* Player */

    @Listener(priority = EventPriority.HIGHEST)
    public void onLogin(MinecraftPlayerLoginConfirmEvent event){
        if(!client.getConnection().isConnected()) return;
        client.sendAction(new PlayerJoinAction(event.getPlayer().getUniqueId()
                ,event.getPlayer().getAsConnectedPlayer().getProtocolVersion().getNumber()));
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onLeave(MinecraftPlayerLogoutEvent event){
        if(!client.getConnection().isConnected()) return;
        client.sendAction(new PlayerLeaveAction(event.getPlayer().getUniqueId()));
    }

}
