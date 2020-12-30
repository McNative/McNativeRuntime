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

import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.plugin.loader.PluginLoader;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.common.Messages;

import java.util.Collection;

public class PluginReloadCommand extends BasicCommand implements Completable {

    private static final String USAGE = "/mcnative plugin reload <plugin>";

    public PluginReloadCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("reload").aliases("r")
                .permission("mcnative.manage.plugin.reload").create());
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_MCNATIVE_INVALID_USAGE, VariableSet.create()
                    .add("command",getConfiguration().getName())
                    .add("usage",USAGE));
            return;
        }

        Plugin<?> plugin = PluginCommandUtil.getPlugin(sender, arguments,getConfiguration().getName(),USAGE);
        if (plugin == null) return;
        PluginCommandUtil.unloadPlugin(sender,plugin);


        PluginLoader loader = McNative.getInstance().getPluginManager().createPluginLoader(arguments[0]);
        if(loader == null){
            sender.sendMessage(Messages.COMMAND_MCNATIVE_PLUGIN_NOTFOUND, VariableSet.create()
                    .add("plugin",arguments[0])
                    .add("plugin.name",arguments[0]));
            return;
        }

        PluginCommandUtil.loadPlugin(sender, loader);
    }

    @Override
    public Collection<String> complete(CommandSender commandSender, String[] arguments) {
        return PluginCommandUtil.tabCompletePlugins(arguments);
    }

}
