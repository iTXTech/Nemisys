package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.TranslationContainer;
import org.itxtech.nemisys.utils.TextFormat;

public class BroadcastCommand extends VanillaCommand {

	public BroadcastCommand(String name) {
		super(name, "Broadcasts a message to every logged in player", "/broadcast <message>");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
		String message = TextFormat.YELLOW + "[Broadcast]: ";
		for (int i = 0; i < args.length; i++) {
            message += args[i] + " ";
        }
		sender.getServer().broadcastMessage(message);
		return true;
	}

}
