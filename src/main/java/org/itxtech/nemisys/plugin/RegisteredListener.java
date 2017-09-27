package org.itxtech.nemisys.plugin;

import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.Event;
import org.itxtech.nemisys.event.EventPriority;
import org.itxtech.nemisys.event.Listener;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RegisteredListener {

    private Listener listener;

    private EventPriority priority;

    private Plugin plugin;

    private EventExecutor executor;

    private boolean ignoreCancelled;

    public RegisteredListener(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
    }

    public Listener getListener() {
        return listener;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void callEvent(Event event) {
        if (event instanceof Cancellable) {
            if (event.isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        executor.execute(listener, event);
    }

    public void destruct() {

    }

    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }
}
