package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.TranslationContainer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StopCommand extends VanillaCommand {

    public StopCommand(String name) {
        super(name, "%nemisys.command.stop.description", "%commands.stop.usage");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        sender.sendMessage(new TranslationContainer("commands.stop.start"));

        sender.getServer().shutdown();

        return true;
    }
}
