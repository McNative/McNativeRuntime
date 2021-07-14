/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 22.03.20, 20:22
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

import net.pretronic.libraries.command.NoPermissionHandler;
import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.event.ClickAction;
import org.mcnative.runtime.api.text.event.HoverAction;
import org.mcnative.runtime.api.text.format.TextColor;
import org.mcnative.runtime.common.Messages;
import org.mcnative.runtime.common.commands.plugin.McNativePluginCommand;
import org.mcnative.runtime.common.commands.util.McNativeUtilityCommand;

public class McNativeCommand extends MainCommand implements NotFindable, NoPermissionHandler {

    public McNativeCommand(ObjectOwner owner,String suffix,String suffix1) {
        super(owner, CommandConfiguration.newBuilder()
                .name("mcnative")
                .aliases("mcn","mcnative"+suffix,"mcn"+suffix,"mcnative"+suffix1,"mcn"+suffix1)
                .permission("mcnative.manage.manage")
                .create());

        registerCommand(new McNativeInfoCommand(owner));
        registerCommand(new McNativePluginCommand(owner));
        registerCommand(new McNativePasteLogCommand(owner));
        registerCommand(new McNativeVersionCommand(owner));
        registerCommand(new McNativeUtilityCommand(owner));
       // registerCommand(new LanguageCommand(owner));
    }

    @Override
    public void commandNotFound(CommandSender sender, String command, String[] args) {
        sender.sendMessage(Messages.COMMAND_MCNATIVE_HELP);
    }

    @Override
    public void handle(CommandSender sender, String permission, String command, String[] strings) {
        sender.sendMessage(Text.newBuilder().text("")
                .onClick(ClickAction.OPEN_URL,"https://mcnative.org")
                .onHover(HoverAction.SHOW_TEXT,"Checkout McNative and build your own plugins")
                .include(builder
                        -> builder.color(TextColor.GOLD).text("McNative")
                        .color(TextColor.DARK_GRAY).text(" | ")
                        .color(TextColor.GOLD).text("Minecraft application framework ")
                        .color(TextColor.RED).text("v"+McNative.getInstance().getApiVersion().getName())
                        .color(TextColor.GOLD).text("by ")
                        .color(TextColor.RED).text("Pretronic (Davide Wietlisbach & Philipp Friedhoff)")).build());
    }
}
