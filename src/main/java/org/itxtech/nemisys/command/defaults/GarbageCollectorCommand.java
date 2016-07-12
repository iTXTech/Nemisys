package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.math.NemisysMath;
import org.itxtech.nemisys.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package org.itxtech.nemisys.command.defaults in project Nukkit .
 */
public class GarbageCollectorCommand extends VanillaCommand {

    public GarbageCollectorCommand(String name) {
        super(name, "%nemisys.command.gc.description", "%nemisys.command.gc.usage");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        long memory = Runtime.getRuntime().freeMemory();

        System.gc();

        long freedMemory = Runtime.getRuntime().freeMemory() - memory;

        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Garbage collection result" + TextFormat.GREEN + " ----");
        sender.sendMessage(TextFormat.GOLD + "Memory freed: " + TextFormat.RED + NemisysMath.round((freedMemory / 1024d / 1024d), 2) + " MB");
        return true;
    }
}
