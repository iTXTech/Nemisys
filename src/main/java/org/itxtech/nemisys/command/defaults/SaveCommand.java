package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.TranslationContainer;
import org.itxtech.nemisys.level.Level;

/**
 * Created on 2015/11/13 by xtypr.
 * Package org.itxtech.nemisys.command.defaults in project Nukkit .
 */
public class SaveCommand extends VanillaCommand {

    public SaveCommand(String name) {
        super(name, "%nukkit.command.save.description", "%commands.save.usage");
        this.setPermission("nukkit.command.save.perform");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.start"));

        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            player.save();
        }

        for (Level level : sender.getServer().getLevels().values()) {
            level.save(true);
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.success"));
        return true;
    }
}
