package org.itxtech.nemisys.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by boybook on 16/6/25.
 */
public class ClientData {

    public Map<String, Entry> clientList = new HashMap<>();

    public class Entry {
        public Entry(String ip, int port, int playerCount, int maxPlayers, String description, float tps, float load, long upTime) {
            this.ip = ip;
            this.port = port;
            this.playerCount = playerCount;
            this.maxPlayers = maxPlayers;
            this.description = description;
            this.tps = tps;
            this.load = load;
            this.upTime = upTime;
        }

        private String ip;
        private int port;
        private int playerCount;
        private int maxPlayers;
        private String description;
        private float tps;
        private float load;
        private long upTime;

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        public String getDescription() {
            return description;
        }

        public float getTicksPerSecond(){
            return this.tps;
        }

        public float getTickUsage(){
            return this.load;
        }

        public long getUpTime(){
            return this.upTime;
        }
    }

    public String getHashByDescription(String description) {
        final String[] re = new String[1];
        this.clientList.forEach((hash, entry) -> {
            if (entry.getDescription().equals(description)) {
                re[0] = hash;
            }
        });
        return re[0];
    }

}
