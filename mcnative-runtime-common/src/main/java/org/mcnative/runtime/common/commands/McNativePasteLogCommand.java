/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 22.03.20, 20:23
 * @web %web%
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

package org.mcnative.runtime.common.commands;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.utility.SystemInfo;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.http.HttpClient;
import net.pretronic.libraries.utility.http.HttpMethod;
import net.pretronic.libraries.utility.http.HttpResult;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.common.Messages;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class McNativePasteLogCommand extends BasicCommand {

    private static final Pattern IPV4_PATTERN = Pattern.compile("((\\\\)|(/))(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])?([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])?(:[0-9][0-9]?[0-9]?[0-9]?[0-9]?)?");

    public McNativePasteLogCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("pasteLog")
                .aliases("paste", "log")
                .permission("mcnative.manage.paste").create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PretronicLogger logger = McNative.getInstance().getLogger();

        String apiVersion = McNative.getInstance().getApiVersion().getName();
        String implementationVersion = McNative.getInstance().getImplementationVersion().getName();
        MinecraftProtocolVersion protocolVersion = McNative.getInstance().getPlatform().getProtocolVersion();
        String javaVersion = SystemUtil.getJavaVersion();

        logger.info("----------------------------------------");
        logger.info("Pretronic Paste");
        logger.info(" ");
        logger.info("McNative Api Version: " + apiVersion);
        logger.info("McNative Imp Version: " + implementationVersion);
        logger.info("Protocol Version: " + protocolVersion.getName() + " | " + protocolVersion.getEdition().getName());
        logger.info("Platform Version: " + McNative.getInstance().getPlatform().getName()+ " - " + McNative.getInstance().getPlatform().getVersion());
        logger.info("Network: " + (McNative.getInstance().isNetworkAvailable() ? McNative.getInstance().getNetwork().getTechnology() : "none"));
        logger.info("Java Version: " + javaVersion);
        logger.info(" ");
        logger.info("Operation system: " + SystemInfo.getOsName() + " version " + SystemInfo.getOsVersion());
        logger.info("OS architecture: " + SystemInfo.getOsArch());
        logger.info(" ");
        logger.info("Max memory: " + Math.round(((double)SystemInfo.getMaxMemory()/(double) (1024 * 1024))));
        logger.info("Free memory: " + Math.round(((double)SystemInfo.getFreeMemory()/(double) (1024 * 1024))));
        logger.info("Allocated memory: " + Math.round(((double)SystemInfo.getAllocatedMemory()/(double) (1024 * 1024))));
        logger.info("Total free memory: " + Math.round(((double)SystemInfo.getTotalFreeMemory()/(double) (1024 * 1024))));
        logger.info("Available Cores: " +Runtime.getRuntime().availableProcessors());
        logger.info(" ");
        logger.info("Plugins: ["+McNative.getInstance().getPluginManager().getPlugins().size()+"]");
        for (Plugin<?> plugin : McNative.getInstance().getPluginManager().getPlugins()) {
            StringBuilder builder = new StringBuilder();
            builder.append("- ")
                    .append(plugin.getName())
                    .append(" v").append(plugin.getDescription().getVersion().getName())
                    .append(" ")
                    .append(plugin.getDescription().getAuthor());

            Collection<String> databases = McNative.getInstance().getPluginManager().getService(ConfigurationProvider.class).getDatabaseTypes(plugin);

            if(!databases.isEmpty()) {
                AtomicBoolean first = new AtomicBoolean(true);
                builder.append(" [Databases: ");
                databases.forEach(type -> builder.append(first.get() ? "" : ",").append(type));
                builder.append("]");
            }

            logger.info(builder.toString());
        }
        logger.info("----------------------------------------");
        sender.sendMessage(Messages.COMMAND_MCNATIVE_PASTE_STARTING);
        McNative.getInstance().getScheduler().createTask(McNative.getInstance()).delay(3, TimeUnit.SECONDS).execute(()-> {
            String content = readFileReversed(McNative.getInstance().getPlatform().getLatestLogLocation());
            if(content == null) return;
            String url = publishLogAndGetUrl(content);
            if(url == null) return;
            logger.info("See your log on " + url);
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PASTE_SUCCESSFUL, VariableSet.create().add("url", url));
        });
    }

    private static String readFileReversed(File file) {
        int lines = 5000;
        int readLines = 0;
        StringBuilder builder = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long fileLength = file.length() - 1;
            randomAccessFile.seek(fileLength);

            for (long pointer = fileLength; pointer >= 0; pointer--) {
                randomAccessFile.seek(pointer);
                char c = (char) randomAccessFile.read();
                if (c == '\n') {
                    readLines++;
                    if (readLines == lines) break;
                }
                builder.append(c);
                fileLength = fileLength - pointer;
            }
            builder.reverse();
        } catch (IOException exception) {
            McNative.getInstance().getLogger().error("Failed to read log");
            McNative.getInstance().getLogger().error("Error: "+ exception.getMessage());
            return null;
        }
        return IPV4_PATTERN.matcher(builder).replaceAll("masked-ipv4");
    }

    private String publishLogAndGetUrl(String content) {
        HttpClient client = new HttpClient();
        client.setUrl("https://paste.pretronic.net/documents");
        client.setRequestMethod(HttpMethod.POST);
        client.setContent(content);
        client.setTLSVersion("TLSv1.2");
        try {
            HttpResult result = client.connect();
            return "https://paste.pretronic.net/" + DocumentFileType.JSON.getReader().read(result.getContent()).getString("key");
        } catch (Exception exception) {
            McNative.getInstance().getLogger().error("Failed to paste log");
            McNative.getInstance().getLogger().error("Error: "+ exception.getMessage());
            return null;
        }
    }
}
