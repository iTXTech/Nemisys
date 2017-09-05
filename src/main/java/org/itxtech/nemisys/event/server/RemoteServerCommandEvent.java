package org.itxtech.nemisys.event.server;

import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.HandlerList;

/**
 * Called when an RCON command is executed.
 *
 * @author Tee7even
 */
public class RemoteServerCommandEvent extends ServerCommandEvent {
    private static final HandlerList handlers = new HandlerList();

    public RemoteServerCommandEvent(CommandSender sender, String command) {
        super(sender, command);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
