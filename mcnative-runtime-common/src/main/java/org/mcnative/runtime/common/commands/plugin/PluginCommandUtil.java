/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.04.20, 20:37
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

package org.mcnative.runtime.common.commands.plugin;

import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.plugin.loader.PluginLoader;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.common.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class PluginCommandUtil {

    private PluginCommandUtil(){}

    protected static Plugin<?> getPlugin(CommandSender sender, String[] arguments,String command,String usage) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_MCNATIVE_INVALID_USAGE, VariableSet.create()
                    .add("command",command)
                    .add("usage",usage));
            return null;
        }
        Plugin<?> plugin = McNative.getInstance().getPluginManager().getPlugin(arguments[0]);
        if(plugin == null || plugin.getName().equalsIgnoreCase("McNative")){
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_NOTFOUND, VariableSet.create()
                    .add("plugin",arguments[0]).add("plugin.name",arguments[0]));
            return null;
        }
        return plugin;
    }

    protected static boolean enablePlugin(CommandSender sender, Plugin<?> plugin) {
        sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_ENABLE_STARTING,VariableSet.create()
                .add("plugin",plugin));

        try{
            plugin.getLoader().bootstrap();//In McNative is enable called bootstrap
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_ENABLE_SUCCESSFULLY, VariableSet.create()
                    .addDescribed("plugin",plugin));
        }catch (Exception exception){
            exception.printStackTrace();
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_ENABLE_FAILED, VariableSet.create()
                    .addDescribed("plugin",plugin)
                    .add("error",exception.getMessage()));
            return false;
        }
        return true;
    }

    protected static boolean disablePlugin(CommandSender sender, Plugin<?> plugin) {
        sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_DISABLE_STARTING, VariableSet.create()
                .addDescribed("plugin",plugin));

        try{
            plugin.getLoader().shutdown();//In McNative is disable called shutdown
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_DISABLE_SUCCESSFULLY,VariableSet.create()
                    .addDescribed("plugin",plugin));
        }catch (Exception exception){
            exception.printStackTrace();
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_DISABLE_FAILED, VariableSet.create()
                    .addDescribed("plugin",plugin)
                    .add("error",exception.getMessage()));
            return false;
        }
        return true;
    }

    protected static boolean unloadPlugin(CommandSender sender, Plugin<?> plugin) {
        sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_UNLOAD_STARTING, VariableSet.create()
                .add("plugin",plugin));

        try{
            plugin.getLoader().unload();//In McNative is enable called UNLOAD

            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_UNLOAD_SUCCESSFULLY, VariableSet.create()
                    .addDescribed("plugin",plugin));
        }catch (Exception exception){
            exception.printStackTrace();
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_UNLOAD_FAILED, VariableSet.create()
                    .addDescribed("plugin",plugin)
                    .add("error",exception.getMessage()));
            return false;
        }
        return true;
    }

    protected static void loadPlugin(CommandSender sender, PluginLoader loader) {
        if (loader.isEnabled()) {
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_ALREADY_ENABLED, VariableSet.create()
                    .addDescribed("plugin", loader.getInstance()));
            return;
        }

        sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_LOAD_STARTING, VariableSet.create()
                .addDescribed("plugin", loader));

        try {
            loader.enable();
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_LOAD_SUCCESSFULLY, VariableSet.create()
                    .addDescribed("plugin", loader.getInstance()));
        } catch (Exception exception) {
            exception.printStackTrace();
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_LOAD_FAILED, VariableSet.create()
                    .addDescribed("plugin", loader.getInstance())
                    .add("exception", exception.getMessage()));
        }
    }

    protected static Collection<String> tabCompletePlugins(String[] arguments) {
        if(arguments.length <= 0){
            Collection<String> result = new ArrayList<>();
            for (Plugin<?> plugin : McNative.getInstance().getPluginManager().getPlugins()) {
                if(!plugin.getName().equalsIgnoreCase("McNative")){
                    result.add(plugin.getName());
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
}
