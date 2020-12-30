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

import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.common.Messages;

public class McNativePluginCommand extends MainCommand implements NotFindable {

    public McNativePluginCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("plugin").aliases("pl","p")
                .permission("mcnative.manage.plugin").create());

        registerCommand(new PluginListCommand(owner));
        registerCommand(new PluginEnableCommand(owner));
        registerCommand(new PluginDisableCommand(owner));
        registerCommand(new PluginRestartCommand(owner));
        registerCommand(new PluginLoadCommand(owner));
        registerCommand(new PluginUnloadCommand(owner));
        registerCommand(new PluginReloadCommand(owner));
    }

    @Override
    public void commandNotFound(CommandSender sender, String s, String[] strings) {
        sender.sendMessage(Messages.COMMAND_MCNATIVE_HELP);
    }
}
