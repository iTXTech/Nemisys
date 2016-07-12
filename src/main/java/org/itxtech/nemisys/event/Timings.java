package org.itxtech.nemisys.event;

import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;
import org.itxtech.nemisys.plugin.PluginManager;
import org.itxtech.nemisys.scheduler.PluginTask;
import org.itxtech.nemisys.scheduler.TaskHandler;

import java.util.HashMap;

/**
 * Created by Pub4Game on 30.06.2016.
 */
public abstract class Timings {

    public static TimingsHandler fullTickTimer;
    public static TimingsHandler serverTickTimer;
    public static TimingsHandler playerNetworkTimer;
    public static TimingsHandler playerNetworkReceiveTimer;
    public static TimingsHandler connectionTimer;
    public static TimingsHandler tickablesTimer;
    public static TimingsHandler schedulerTimer;
    public static TimingsHandler serverCommandTimer;
    public static TimingsHandler schedulerSyncTimer;
    public static TimingsHandler schedulerAsyncTimer;
    private static HashMap<Byte, TimingsHandler> packetReceiveTimingMap = new HashMap<>();
    private static HashMap<Byte, TimingsHandler> packetSendTimingMap = new HashMap<>();
    private static HashMap<String, TimingsHandler> pluginTaskTimingMap = new HashMap<>();

    public static void init() {
        if (serverTickTimer != null) {
            return;
        }
        fullTickTimer = new TimingsHandler("Full Server Tick");
        serverTickTimer = new TimingsHandler("** Full Server Tick", fullTickTimer);
        playerNetworkTimer = new TimingsHandler("Player Network Send");
        playerNetworkReceiveTimer = new TimingsHandler("Player Network Receive");
        connectionTimer = new TimingsHandler("Connection Handler");
        tickablesTimer = new TimingsHandler("Tickables");
        schedulerTimer = new TimingsHandler("Scheduler");
        serverCommandTimer = new TimingsHandler("Server Command");
        schedulerSyncTimer = new TimingsHandler("** Scheduler - Sync Tasks", PluginManager.pluginParentTimer);
        schedulerAsyncTimer = new TimingsHandler("** Scheduler - Async Tasks");
    }

    public static TimingsHandler getPluginTaskTimings(TaskHandler task, long period) {
        Runnable ftask = task.getTask();
        String plugin;
        if (ftask instanceof PluginTask && ((PluginTask) ftask).getOwner() != null) {
            plugin = ((PluginTask) ftask).getOwner().getDescription().getFullName();
        } else if (!task.timingName.isEmpty()) {
            plugin = "Scheduler";
        } else {
            plugin = "Unknown";
        }
        String taskname = task.getTaskName();
        String name = "Task: " + plugin + " Runnable: " + taskname;
        if (period > 0) {
            name += "(interval:" + period + ")";
        } else {
            name += "(Single)";
        }
        if (!pluginTaskTimingMap.containsKey(name)) {
            pluginTaskTimingMap.put(name, new TimingsHandler(name, schedulerSyncTimer));
        }
        return pluginTaskTimingMap.get(name);
    }

    public static TimingsHandler getReceiveDataPacketTimings(DataPacket pk) {
        if (!packetReceiveTimingMap.containsKey(pk.pid())) {
            String pkName = pk.getClass().getSimpleName();
            packetReceiveTimingMap.put(pk.pid(), new TimingsHandler("** receivePacket - " + pkName + " [0x" + Integer.toHexString(pk.pid()) + "]", playerNetworkReceiveTimer));
        }
        return packetReceiveTimingMap.get(pk.pid());
    }

    public static TimingsHandler getSendDataPacketTimings(DataPacket pk) {
        if (!packetSendTimingMap.containsKey(pk.pid())) {
            String pkName = pk.getClass().getSimpleName();
            packetSendTimingMap.put(pk.pid(), new TimingsHandler("** sendPacket - " + pkName + " [0x" + Integer.toHexString(pk.pid()) + "]", playerNetworkTimer));
        }
        return packetSendTimingMap.get(pk.pid());
    }
}
