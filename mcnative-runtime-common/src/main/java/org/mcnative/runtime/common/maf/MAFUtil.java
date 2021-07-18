package org.mcnative.runtime.common.maf;

import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.utility.SystemInfo;
import net.pretronic.libraries.utility.SystemUtil;
import org.mcnative.actionframework.sdk.actions.server.ServerInfoAction;
import org.mcnative.actionframework.sdk.actions.server.ServerStartupAction;
import org.mcnative.actionframework.sdk.actions.server.ServerStatusAction;
import org.mcnative.actionframework.sdk.client.MAFClient;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.MinecraftPlatform;
import org.mcnative.runtime.api.ServerPerformance;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;

import java.util.Arrays;
import java.util.Collection;

public class MAFUtil {

    protected static void sendStartupAction(MAFClient client){
        if(!client.getConnection().isConnected()) return;
        McNative runtime = McNative.getInstance();
        MinecraftPlatform platform =  runtime.getPlatform();

        String networkTechnology = "none";
        if(runtime.isNetworkAvailable()) {
            networkTechnology = runtime.getNetwork().getTechnology();
        }

        Collection<MinecraftProtocolVersion> protocolVersions = platform.getJoinableProtocolVersions();
        int[] protocols = new int[protocolVersions.size()];
        int index = 0;
        for (MinecraftProtocolVersion version : protocolVersions) {
            protocols[index] = version.getNumber();
            index++;
        }

        client.sendAction(new ServerStartupAction(
                runtime.getLocal().getName()
                ,runtime.getLocal().getAddress()
                ,runtime.getLocal().getGroup()
                ,platform.getName()
                ,platform.getVersion()
                ,platform.isProxy()
                ,networkTechnology
                ,platform.getProtocolVersion().getNumber()
                ,protocols
                ,runtime.getApiVersion().getBuild()
                , SystemInfo.getOsName()
                ,SystemInfo.getOsArch()
                , SystemUtil.getJavaVersion()
                ,SystemInfo.getDeviceId()
                ,(int) Math.round(((double)SystemInfo.getMaxMemory()/(double) (1024 * 1024)))
                ,Runtime.getRuntime().availableProcessors()));
    }

    protected static void sendInfoAction(MAFClient client){
        if(!client.getConnection().isConnected()) return;
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

    protected static void sendStatusAction(MAFClient client) {
        if(!MAFListener.STARTUP || !client.getConnection().isConnected()) return;
        ServerPerformance performance = McNative.getInstance().getLocal().getServerPerformance();
        float[] tps = performance.getRecentTps();
        if(tps.length > 3) {
            tps = Arrays.copyOfRange(tps, tps.length-3, tps.length);
        }

        ServerStatusAction action = new ServerStatusAction(McNative.getInstance().getLocal().getMaxPlayerCount(),
                tps,
                performance.getUsedMemory(),
                performance.getCpuUsage());
        client.sendAction(action);
    }

}
