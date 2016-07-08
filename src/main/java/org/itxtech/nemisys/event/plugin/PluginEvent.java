package org.itxtech.nemisys.event.plugin;

import org.itxtech.nemisys.event.Event;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
