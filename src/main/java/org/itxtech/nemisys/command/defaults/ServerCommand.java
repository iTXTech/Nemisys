package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.command.data.CommandParamType;
import org.itxtech.nemisys.command.data.CommandParameter;
import org.itxtech.nemisys.event.TranslationContainer;
import org.itxtech.nemisys.utils.TextFormat;

/**
 * @author CreeperFace
 */
public class ServerCommand extends VanillaCommand {

    public ServerCommand(String name) {
        super(name, "%nemisys.command.server.description", "%commands.server.usage");
        this.setGlobal(true);
        this.setPermission("nemisys.command.server");

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("server", CommandParamType.RAWTEXT, false),
                new CommandParameter("player", CommandParamType.TARGET, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String server = args[0];
        Player player;

        if (args.length == 1) {
            if (!sender.isPlayer()) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return false;
            }

            player = (Player) sender;
        } else {
            player = sender.getServer().getPlayerExact(args[1]);
        }

        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }

        Client target = sender.getServer().getClientByDesc(server);
        if (target == null) {
            sender.sendMessage(sender.getServer().getLanguage().translateString(TextFormat.RED + "%commands.server.notFound", server));
            return true;
        }

        player.transfer(target);
        player.sendMessage(sender.getServer().getLanguage().translateString(TextFormat.RED + "%commands.server.success." + (sender == player ? "self" : "other"), server));
        return true;
    }
}
