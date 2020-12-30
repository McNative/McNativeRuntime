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

package org.mcnative.runtime.common.commands;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.SystemInfo;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.common.Messages;

public class McNativeInfoCommand extends BasicCommand {

    public McNativeInfoCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("info")
                .aliases("i")
                .permission("mcnative.manage.info")
                .create());
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        String javaVersion = SystemUtil.getJavaVersion();
        String network = McNative.getInstance().isNetworkAvailable() ? McNative.getInstance().getNetwork().getTechnology(): "None";

        sender.sendMessage(Messages.COMMAND_MCNATIVE_INFO, VariableSet.create()
                .add("platform.name", McNative.getInstance().getPlatform().getName())
                .add("platform.version", McNative.getInstance().getPlatform().getVersion())
                .add("platform.name", McNative.getInstance().getPlatform().getName())
                .add("protocol.version", McNative.getInstance().getPlatform().getProtocolVersion().getName())
                .add("protocol.edition", McNative.getInstance().getPlatform().getProtocolVersion().getEdition())
                .add("mcnative.version.api", McNative.getInstance().getApiVersion().getName())
                .add("mcnative.version.implementation", McNative.getInstance().getImplementationVersion().getName())
                .add("network", network)
                .add("java.version", javaVersion)
                .add("os.name", SystemInfo.getOsName())
                .add("os.version", SystemInfo.getOsVersion())
                .add("os.arch", SystemInfo.getOsArch())
                .add("memory.maximum", Math.round(((double)SystemInfo.getMaxMemory())))
                .add("memory.allocated", Math.round(((double)SystemInfo.getAllocatedMemory()/(double) (1024 * 1024))))
                .add("memory.free", Math.round(((double)SystemInfo.getFreeMemory()/(double) (1024 * 1024))))
                .add("memory.free.total", Math.round(((double)SystemInfo.getTotalFreeMemory()/(double) (1024 * 1024)))));
    }
}
