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
import org.mcnative.actionframework.sdk.client.MAFClient;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.MinecraftPlatform;
import org.mcnative.runtime.api.event.player.MinecraftPlayerLogoutEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.runtime.api.event.service.local.LocalServiceReloadEvent;
import org.mcnative.runtime.api.event.service.local.LocalServiceShutdownEvent;
import org.mcnative.runtime.api.event.service.local.LocalServiceStartupEvent;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class MAFListener {

    private final MAFClient client;

    public MAFListener(MAFClient client) {
        this.client = client;
    }

    /* Local Service */

    @Listener(priority = EventPriority.HIGHEST)
    public void onStartup(LocalServiceStartupEvent event){
        MinecraftPlatform platform =  event.getRuntime().getPlatform();

        String networkTechnology = "none";
        if(event.getRuntime().isNetworkAvailable()) {
            networkTechnology = event.getRuntime().getNetwork().getTechnology();
        }

        Collection<MinecraftProtocolVersion> protocolVersions = platform.getJoinableProtocolVersions();
        int[] protocols = new int[protocolVersions.size()];
        int index = 0;
        for (MinecraftProtocolVersion version : protocolVersions) {
            protocols[index] = version.getNumber();
            index++;
        }

        //@Todo implement server group
        client.sendAction(new ServerStartupAction(
                event.getRuntime().getLocal().getName()
                ,event.getRuntime().getLocal().getAddress(),"Unknown"
                ,platform.getName()
                ,platform.getVersion()
                ,platform.isProxy()
                ,networkTechnology
                ,platform.getProtocolVersion().getNumber()
                ,protocols
                ,event.getRuntime().getApiVersion().getBuild()
                ,SystemInfo.getOsName()
                ,SystemInfo.getOsArch()
                ,SystemUtil.getJavaVersion()
                ,SystemInfo.getDeviceId()
                ,(int) Math.round(((double)SystemInfo.getMaxMemory()/(double) (1024 * 1024)))
                ,Runtime.getRuntime().availableProcessors()));

        event.getRuntime().getScheduler().createTask(ObjectOwner.SYSTEM).delay(20, TimeUnit.SECONDS)
                .execute(this::sendInfoAction);
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onStartup(LocalServiceReloadEvent event){
        sendInfoAction();
    }

    private void sendInfoAction(){
        Collection<Plugin<?>> plugins = McNative.getInstance().getPluginManager().getPlugins();
        ServerInfoAction.Plugin[] pluginInfo = new ServerInfoAction.Plugin[plugins.size()];
        int index = 0;
        for (Plugin<?> plugin : plugins) {
            pluginInfo[index] = new ServerInfoAction.Plugin(plugin.getDescription().getId()
                    ,plugin.getDescription().getName()
                    ,plugin.getDescription().getVersion().getName());
            index++;
        }

        Collection<String> drivers = McNative.getInstance().getRegistry().getService(ConfigurationProvider.class).getDatabaseTypes();
        client.sendAction(new ServerInfoAction(pluginInfo,drivers.toArray(new String[]{})));
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onShutdown(LocalServiceShutdownEvent event){
        client.sendAction(new ServerShutdownAction());
    }

    /* Player */

    @Listener(priority = EventPriority.HIGHEST)
    public void onLogin(MinecraftPlayerPostLoginEvent event){
        client.sendAction(new PlayerJoinAction(event.getPlayer().getUniqueId()
                ,event.getPlayer().getAsConnectedPlayer().getProtocolVersion().getNumber()));
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onLeave(MinecraftPlayerLogoutEvent event){
        client.sendAction(new PlayerLeaveAction(event.getPlayer().getUniqueId()));
    }

}
