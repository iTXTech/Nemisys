package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.TranslationContainer;

/**
 * Created on 2015/11/11 by xtypr.
 * Package org.itxtech.nemisys.command.defaults in project Nukkit .
 */
public class ListCommand extends VanillaCommand {

    public ListCommand(String name) {
        super(name, "%nukkit.command.list.description", "%commands.players.usage");
        this.setPermission("nukkit.command.list");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        String online = "";
        int onlineCount = 0;
        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            if (player.isOnline() && (!(sender instanceof Player) || ((Player) sender).canSee(player))) {
                online += player.getDisplayName() + ", ";
                ++onlineCount;
            }
        }

        if (online.length() > 0) {
            online = online.substring(0, online.length() - 2);
        }

        sender.sendMessage(new TranslationContainer("commands.players.list",
                new String[]{String.valueOf(onlineCount), String.valueOf(sender.getServer().getMaxPlayers())}));
        sender.sendMessage(online);
        return true;
    }
}
