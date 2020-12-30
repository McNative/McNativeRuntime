/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 22.03.20, 20:25
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

package org.mcnative.runtime.common.commands.util;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.tablist.Tablist;

public class ReloadTablistCommand extends BasicCommand {

    public ReloadTablistCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("reload-tablist")
                .aliases("rt")
                .create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Tablist tablist = McNative.getInstance().getLocal().getServerTablist();
        if(tablist == null){
            sender.sendMessage("[MCN Utility] No default server tablist found.");
            return;
        }

        if(args.length == 0){
            tablist.update();
            sender.sendMessage("[MCN Utility] Tablist reloaded");
            return;
        }else if(args.length >= 2){
            String action = args[0];
            String target = args[1];

            ConnectedMinecraftPlayer player = McNative.getInstance().getLocal().getConnectedPlayer(target);
            if(player == null){
                sender.sendMessage("[MCN Utility] Player not found");
                return;
            }

            if(action.equalsIgnoreCase("for")){
                tablist.update(player);
                sender.sendMessage("[MCN Utility] Tablist for "+player.getName()+" reloaded");
                return;
            }else if(action.equalsIgnoreCase("of")){
                sender.sendMessage("[MCN Utility] Tablist entry of "+player.getName()+" reloaded");
                return;
            }
        }
        sender.sendMessage("[MCN Utility] Tablist Help");
        sender.sendMessage("/mcn utility tablist-reload | Reload the whole tablist");
        sender.sendMessage("/mcn utility tablist-reload for <player> | Reload the tablist for the specified player");
        sender.sendMessage("/mcn utility tablist-reload of <player> | Reload the specified player in the tablist");
    }
}
