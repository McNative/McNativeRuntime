package org.mcnative.runtime.common.commands;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;

public class LanguageCommand extends BasicCommand {

    private final MessageProvider messageProvider;

    public LanguageCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("language")
                .aliases("l")
                .permission("mcnative.language")
                .create());
        messageProvider = McNative.getInstance().getRegistry().getService(MessageProvider.class);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        //Language functions are missing
    }
}
