/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 25.08.19, 19:22
 *
 * The McNative Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.mcnative.bungeecord;

import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.configuration.DefaultCommandConfiguration;
import net.pretronic.libraries.document.DocumentRegistry;
import net.pretronic.libraries.event.DefaultEventBus;
import net.pretronic.libraries.logging.bridge.JdkPretronicLogger;
import net.pretronic.libraries.plugin.description.PluginVersion;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.reflect.ReflectionUtil;
import net.pretronic.libraries.utility.reflect.UnsafeInstanceCreator;
import org.mcnative.bungeecord.event.McNativeBridgeEventHandler;
import org.mcnative.bungeecord.network.McNativeGlobalActionListener;
import org.mcnative.bungeecord.network.McNativePlayerActionListener;
import org.mcnative.bungeecord.network.bungeecord.BungeecordProxyNetwork;
import org.mcnative.bungeecord.network.cloudnet.CloudNetV2PlatformListener;
import org.mcnative.bungeecord.network.cloudnet.CloudNetV3PlatformListener;
import org.mcnative.bungeecord.player.BungeeCordPlayerManager;
import org.mcnative.bungeecord.plugin.BungeeCordPluginManager;
import org.mcnative.bungeecord.plugin.McNativeEventBus;
import org.mcnative.bungeecord.plugin.command.BungeeCordCommandManager;
import org.mcnative.bungeecord.server.BungeeCordServerMap;
import org.mcnative.common.McNative;
import org.mcnative.common.network.Network;
import org.mcnative.common.network.component.server.ServerStatusResponse;
import org.mcnative.common.network.event.NetworkEventHandler;
import org.mcnative.common.player.chat.ChatChannel;
import org.mcnative.common.player.chat.GroupChatFormatter;
import org.mcnative.common.protocol.packet.DefaultPacketManager;
import org.mcnative.common.serviceprovider.statistics.McNativeStatisticService;
import org.mcnative.network.integrations.cloudnet.v2.CloudNetV2Network;
import org.mcnative.network.integrations.cloudnet.v3.CloudNetV3Network;
import org.mcnative.proxy.ProxyService;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class McNativeLauncher {

    private static Plugin PLUGIN;
    private static McNativeStatisticService STATISTIC_SERVICE;

    public static Plugin getPlugin() {
        return PLUGIN;
    }

    public static void launchMcNative(){
        launchMcNativeInternal(setupDummyPlugin());
    }

    public static void launchMcNativeInternal(Plugin plugin){
        if(McNative.isAvailable()) return;
        PluginVersion version = PluginVersion.ofImplementation(McNativeLauncher.class);
        PLUGIN = plugin;
        Logger logger = ProxyServer.getInstance().getLogger();
        logger.info(McNative.CONSOLE_PREFIX+"McNative is starting, please wait...");
        logger.info(McNative.CONSOLE_PREFIX+"Version: "+version.getName());
        ProxyServer proxy = ProxyServer.getInstance();

        DocumentRegistry.getDefaultContext().registerMappingAdapter(CommandConfiguration.class, DefaultCommandConfiguration.class);

        if(!McNativeBungeeCordConfiguration.load(new JdkPretronicLogger(logger),new File("plugins/McNative/"))) return;

        BungeeCordServerMap serverMap = new BungeeCordServerMap();
        logger.info(McNative.CONSOLE_PREFIX+"McNative initialised and injected server map.");

        BungeeCordPluginManager pluginManager = new BungeeCordPluginManager();
        logger.info(McNative.CONSOLE_PREFIX+"McNative initialised plugin manager.");

        BungeeCordPlayerManager playerManager = new BungeeCordPlayerManager();
        logger.info(McNative.CONSOLE_PREFIX+"McNative initialised player manager.");

        BungeeCordCommandManager commandManager = new BungeeCordCommandManager(pluginManager,ProxyServer.getInstance().getPluginManager());
        logger.info(McNative.CONSOLE_PREFIX+"McNative initialised and injected command manager.");

        BungeeCordService localService = new BungeeCordService(new DefaultPacketManager(),commandManager,playerManager
                ,new DefaultEventBus(new NetworkEventHandler()),serverMap);
        BungeeCordMcNative instance = new BungeeCordMcNative(version,pluginManager,playerManager,null, localService);
        McNative.setInstance(instance);
        instance.setNetwork(setupNetwork(logger,localService,instance.getExecutorService(),serverMap));

        instance.getNetwork().getMessenger().registerChannel("mcnative_player",ObjectOwner.SYSTEM,new McNativePlayerActionListener());
        instance.getNetwork().getMessenger().registerChannel("mcnative_global",ObjectOwner.SYSTEM,new McNativeGlobalActionListener());

        instance.registerDefaultProviders();
        instance.registerDefaultCommands();
        instance.registerDefaultDescribers();

        proxy.setConfigurationAdapter(new McNativeConfigurationAdapter(serverMap,proxy.getConfigurationAdapter()));
        logger.info(McNative.CONSOLE_PREFIX+"McNative has overwritten the configuration adapter.");

        McNativeEventBus eventBus = new McNativeEventBus(localService.getEventBus());
        logger.info(McNative.CONSOLE_PREFIX+"McNative initialised and injected event bus.");

        new McNativeBridgeEventHandler(eventBus,localService.getEventBus(),playerManager,serverMap);
        logger.info(McNative.CONSOLE_PREFIX+"McNative has overwritten default bungeecord events.");

        McNativeBungeeCordConfiguration.postLoad();
        setupConfiguredServices();

        instance.setReady(true);
        STATISTIC_SERVICE = new McNativeStatisticService();

        //ResourceMessageExtractor.extractMessages(McNativeLauncher.class.getClassLoader(),"system-messages/","McNative");

        logger.info(McNative.CONSOLE_PREFIX+"McNative successfully started.");
    }

    private static Network setupNetwork(Logger logger,ProxyService proxy,ExecutorService executor, BungeeCordServerMap serverMap){
        if(ProxyServer.getInstance().getPluginManager().getPlugin("CloudNetAPI") != null){
            logger.info(McNative.CONSOLE_PREFIX+"(Network) Initialized CloudNet V2 networking technology");
            CloudNetV2Network network = new CloudNetV2Network(executor);
            new CloudNetV2PlatformListener(network.getMessenger());
            return network;
        }else if(ProxyServer.getInstance().getPluginManager().getPlugin("CloudNet-Bridge") != null){
            logger.info(McNative.CONSOLE_PREFIX+"(Network) Initialized CloudNet V3 networking technology");
            CloudNetV3Network network = new CloudNetV3Network(executor);
            new CloudNetV3PlatformListener(network.getMessenger());
            return network;
        }else{
            logger.info(McNative.CONSOLE_PREFIX+"(Network) Initialized BungeeCord networking technology");
            return new BungeecordProxyNetwork(proxy,executor,serverMap);
        }
    }

    private static void setupConfiguredServices(){
        if(McNativeBungeeCordConfiguration.PLAYER_GLOBAL_CHAT_ENABLED){
            ChatChannel serverChat = ChatChannel.newChatChannel();
            serverChat.setName("ServerChat");
            serverChat.setMessageFormatter((GroupChatFormatter) (sender, variables, message) -> McNativeBungeeCordConfiguration.PLAYER_GLOBAL_CHAT);
            McNative.getInstance().getLocal().setServerChat(serverChat);
        }

        //Motd setup
        File serverIcon = new File("server-icon.png");
        if(!serverIcon.exists()){
            try{
                McNativeBridgeEventHandler.DEFAULT_FAVICON = Favicon.create(ImageIO.read(ServerStatusResponse.DEFAULT_FAVICON_URL));
            }catch (Exception ignored){}
        }
    }

    protected static void shutdown(){
        if(!McNative.isAvailable()) return;
        Logger logger = ProxyServer.getInstance().getLogger();
        logger.info(McNative.CONSOLE_PREFIX+"McNative is stopping, please wait...");
        McNative instance = McNative.getInstance();

        if(instance != null){
            if(STATISTIC_SERVICE != null) STATISTIC_SERVICE.shutdown();

            instance.getLogger().shutdown();
            instance.getScheduler().shutdown();
            instance.getExecutorService().shutdown();
            instance.getPluginManager().shutdown();
            ((BungeeCordMcNative)instance).setReady(false);
        }
    }

    @SuppressWarnings("unchecked")
    private static Plugin setupDummyPlugin(){
        PluginDescription description = new PluginDescription();
        description.setName("McNative");
        description.setVersion(McNativeLauncher.class.getPackage().getImplementationVersion());
        description.setAuthor("Pretronic and McNative contributors");
        description.setMain("reflected");

        Plugin plugin = UnsafeInstanceCreator.newInstance(DummyPlugin.class);
        ReflectionUtil.invokeMethod(Plugin.class,plugin,"init"
                ,new Class[]{ProxyServer.class,PluginDescription.class}
                ,new Object[]{ProxyServer.getInstance(),description});

        Map<String, Plugin> plugins = ReflectionUtil.getFieldValue(PluginManager.class,ProxyServer.getInstance().getPluginManager(),"plugins", Map.class);
        plugins.put(description.getName(),plugin);
        return plugin;
    }

    private static class DummyPlugin extends Plugin{

        @Override
        public void onDisable() {
            McNativeLauncher.shutdown();
        }
    }

}
