package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.TranslationContainer;

/**
 * Created on 2015/11/13 by xtypr.
 * Package org.itxtech.nemisys.command.defaults in project Nukkit .
 */
public class SaveOnCommand extends VanillaCommand {

    public SaveOnCommand(String name) {
        super(name, "%nukkit.command.saveon.description", "%commands.save-on.usage");
        this.setPermission("nukkit.command.save.enable");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(true);
        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.enabled"));
        return true;
    }
}
