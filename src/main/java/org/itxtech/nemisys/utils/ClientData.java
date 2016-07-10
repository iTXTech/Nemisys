package org.itxtech.nemisys.utils;

import org.itxtech.nemisys.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by boybook on 16/6/25.
 */
public class ClientData {

    public Map<String, Entry> clientList = new HashMap<>();

    public class Entry {
        public Entry(String ip, int port, int playerCount, int maxPlayers, String description) {
            this.ip = ip;
            this.port = port;
            this.playerCount = playerCount;
            this.maxPlayers = maxPlayers;
            this.description = description;
        }

        private String ip;
        private int port;
        private int playerCount;
        private int maxPlayers;
        private String description;

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
    }

}
