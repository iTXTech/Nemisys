package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.IPlayer;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.TranslationContainer;
import org.itxtech.nemisys.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package org.itxtech.nemisys.command.defaults in project Nukkit .
 */
public class OpCommand extends VanillaCommand {

    public OpCommand(String name) {
        super(name, "%nukkit.command.op.description", "%commands.op.usage");
        this.setPermission("nukkit.command.op.give");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String name = args[0];
        IPlayer player = sender.getServer().getOfflinePlayer(name);

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.op.success", player.getName()));
        if (player instanceof Player) {
            ((Player) player).sendMessage(TextFormat.GRAY + "You are now op!");
        }

        player.setOp(true);

        return true;
    }
}
