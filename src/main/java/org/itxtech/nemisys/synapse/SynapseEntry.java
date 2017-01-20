package org.itxtech.nemisys.synapse;

import com.google.gson.Gson;
import org.itxtech.nemisys.Nemisys;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.event.synapse.player.SynapsePlayerCreationEvent;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;
import org.itxtech.nemisys.network.protocol.spp.*;
import org.itxtech.nemisys.synapse.network.SynLibInterface;
import org.itxtech.nemisys.synapse.network.SynapseInterface;
import org.itxtech.nemisys.utils.ClientData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by boybook on 16/8/21.
 */
public class SynapseEntry {

    private Synapse synapse;

    private boolean enable;
    private String serverIp;
    private int port;
    private boolean isMainServer;
    private String password;
    private SynapseInterface synapseInterface;
    private boolean verified = false;
    private long lastUpdate;
    private long lastRecvInfo;
    private Map<UUID, SynapsePlayer> players = new HashMap<>();
    private SynLibInterface synLibInterface;
    private ClientData clientData;
    private String serverDescription;

    public Synapse getSynapse() {
        return this.synapse;
    }

    public SynapseEntry(Synapse synapse, String serverIp, int port, boolean isMainServer, String password, String serverDescription) {
        this.synapse = synapse;
        this.serverIp = serverIp;
        this.port = port;
        this.isMainServer = isMainServer;
        this.password = password;
        if (this.password.length() != 16) {
            synapse.getLogger().warning("You must use a 16 bit length key!");
            synapse.getLogger().warning("This SynapseAPI Entry will not be enabled!");
            enable = false;
            return;
        }
        this.serverDescription = serverDescription;

        this.synapseInterface = new SynapseInterface(this, this.serverIp, this.port);
        this.synLibInterface = new SynLibInterface(this.synapseInterface);
        this.lastUpdate = System.currentTimeMillis();
        this.lastRecvInfo = System.currentTimeMillis();
        this.getSynapse().getServer().getScheduler().scheduleRepeatingTask(new Ticker(), 5);
    }

    public boolean isEnable() {
        return enable;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public SynapseInterface getSynapseInterface() {
        return synapseInterface;
    }

    public void shutdown(){
        if(this.verified){
            DisconnectPacket pk = new DisconnectPacket();
            pk.type = DisconnectPacket.TYPE_GENERIC;
            pk.message = "Server closed";
            this.sendDataPacket(pk);
            this.getSynapse().getLogger().debug("Synapse client has disconnected from Synapse synapse");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //ignore
            }
        }
        if (this.synapseInterface != null) this.synapseInterface.shutdown();
    }

    public String getServerDescription() {
        return serverDescription;
    }

    public void setServerDescription(String serverDescription) {
        this.serverDescription = serverDescription;
    }

    public void sendDataPacket(SynapseDataPacket pk){
        this.synapseInterface.putPacket(pk);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setMainServer(boolean mainServer) {
        isMainServer = mainServer;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getPort() {
        return port;
    }

    public void broadcastPacket(SynapsePlayer[] players, DataPacket packet){
        this.broadcastPacket(players, packet, false);
    }

    public void broadcastPacket(SynapsePlayer[] players, DataPacket packet, boolean direct){
        packet.encode();
        BroadcastPacket broadcastPacket = new BroadcastPacket();
        broadcastPacket.direct = direct;
        broadcastPacket.payload = packet.getBuffer();
        broadcastPacket.entries = new ArrayList<>();
        for (SynapsePlayer player : players){
            broadcastPacket.entries.add(player.getUniqueId());
        }
        this.sendDataPacket(broadcastPacket);
    }

    public boolean isMainServer() {
        return isMainServer;
    }

    public String getHash() {
        return this.serverIp + ":" + this.port;
    }

    public void connect(){
        this.getSynapse().getLogger().notice("Connecting " + this.getHash());
        this.verified = false;
        ConnectPacket pk = new ConnectPacket();
        pk.password = this.password;
        pk.isMainServer = this.isMainServer();
        pk.description = this.serverDescription;
        pk.maxPlayers = this.getSynapse().getServer().getMaxPlayers();
        pk.protocol = SynapseInfo.CURRENT_PROTOCOL;
        this.sendDataPacket(pk);
        /*
        Thread ticker = new Thread(new Ticker());
        ticker.setName("SynapseAPI Ticker");
        ticker.start();
        */
    }

    public class Ticker implements Runnable {
        @Override
        public void run() {
            tick();
        }
    }

    public void tick(){
        this.synapseInterface.process();
        if (!this.getSynapseInterface().isConnected()) return;
        long time = System.currentTimeMillis();
        if((time - this.lastUpdate) >= 5000){//Heartbeat!
            this.lastUpdate = time;
            HeartbeatPacket pk = new HeartbeatPacket();
            pk.tps = this.getSynapse().getServer().getTicksPerSecondAverage();
            pk.load = this.getSynapse().getServer().getTickUsageAverage();
            pk.upTime = (System.currentTimeMillis() - Nemisys.START_TIME) / 1000;
            this.sendDataPacket(pk);
            this.getSynapse().getServer().getLogger().debug(time + " -> Sending Heartbeat Packet to " + this.getHash());
        }

        /*
        for (int i = 0; i < new Random().nextInt(10) + 1; i++) {
            InformationPacket test = new InformationPacket();
            test.type = InformationPacket.TYPE_PLUGIN_MESSAGE;
            test.message = getRandomString(1024 * (new Random().nextInt(20) + 110));
            this.sendDataPacket(test);
        }*/

        long finalTime = System.currentTimeMillis();
        long usedTime = finalTime - time;
        //this.getSynapse().getServer().getLogger().warning(time + " -> tick 用时 " + usedTime + " 毫秒");
        if(((finalTime - this.lastUpdate) >= 30000) && this.synapseInterface.isConnected()){  //30 seconds timeout
            this.synapseInterface.reconnect();
        }
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public void removePlayer(SynapsePlayer player){
        UUID uuid = player.getUniqueId();
        if(this.players.containsKey(uuid)){
            this.players.remove(uuid);
        }
    }

    public void removePlayer(UUID uuid){
        if(this.players.containsKey(uuid)){
            this.players.remove(uuid);
        }
    }

    public void handleDataPacket(SynapseDataPacket pk){
        //this.getSynapse().getLogger().warning("Received packet " + pk.pid() + "(" + pk.getClass().getSimpleName() + ") from " + this.serverIp + ":" + this.port);
        switch(pk.pid()){
            case SynapseInfo.DISCONNECT_PACKET:
                DisconnectPacket disconnectPacket = (DisconnectPacket) pk;
                this.verified = false;
                switch(disconnectPacket.type){
                    case DisconnectPacket.TYPE_GENERIC:
                        this.getSynapse().getLogger().notice("Synapse Client has disconnected due to " + disconnectPacket.message);
                        this.synapseInterface.reconnect();
                        break;
                    case DisconnectPacket.TYPE_WRONG_PROTOCOL:
                        this.getSynapse().getLogger().error(disconnectPacket.message);
                        break;
                }
                break;
            case SynapseInfo.INFORMATION_PACKET:
                InformationPacket informationPacket = (InformationPacket)pk;
                switch(informationPacket.type){
                    case InformationPacket.TYPE_LOGIN:
                        if (informationPacket.message.equals(InformationPacket.INFO_LOGIN_SUCCESS)){
                            this.getSynapse().getLogger().notice("Login success to " + this.serverIp + ":" + this.port);
                            this.verified = true;
                        } else if(informationPacket.message.equals(InformationPacket.INFO_LOGIN_FAILED)){
                            this.getSynapse().getLogger().notice("Login failed to " + this.serverIp + ":" + this.port);
                        }
                        break;
                    case InformationPacket.TYPE_CLIENT_DATA:
                        this.clientData = new Gson().fromJson(informationPacket.message, ClientData.class);
                        this.lastRecvInfo = System.currentTimeMillis();
                        //this.getSynapse().getLogger().debug("Received ClientData from " + this.serverIp + ":" + this.port);
                        break;
                }
                break;
            case SynapseInfo.PLAYER_LOGIN_PACKET:
                PlayerLoginPacket playerLoginPacket = (PlayerLoginPacket)pk;
                SynapsePlayerCreationEvent ev = new SynapsePlayerCreationEvent(this.synLibInterface, SynapsePlayer.class, SynapsePlayer.class, new Random().nextLong(), playerLoginPacket.address, playerLoginPacket.port);
                this.getSynapse().getServer().getPluginManager().callEvent(ev);
                Class<? extends SynapsePlayer> clazz = ev.getPlayerClass();
                try {
                    Constructor constructor = clazz.getConstructor(SourceInterface.class, SynapseEntry.class, Long.class, String.class, int.class);
                    SynapsePlayer player = (SynapsePlayer) constructor.newInstance(this.synLibInterface, this, ev.getClientId(), ev.getAddress(), ev.getPort());
                    player.setUniqueId(playerLoginPacket.uuid);
                    this.players.put(playerLoginPacket.uuid, player);
                    this.getSynapse().getServer().addPlayer(playerLoginPacket.uuid.toString(), player);
                    player.handleLoginPacket(playerLoginPacket);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    Server.getInstance().getLogger().logException(e);
                }
                break;
            case SynapseInfo.REDIRECT_PACKET:
                RedirectPacket redirectPacket = (RedirectPacket)pk;
                UUID uuid = redirectPacket.uuid;
                if(this.players.containsKey(uuid)){
                    Player player = this.players.get(uuid);
                    DataPacket pk0 = this.getSynapse().getPacket(redirectPacket.mcpeBuffer);
                    if(pk0 != null) {
                        pk0.decode();
                        player.handleDataPacket(pk0);
                    } else {
                        if (player.getClient() != null) player.redirectPacket(redirectPacket.mcpeBuffer); //player.getClient().sendDataPacket(redirectPacket);
                    }
                }
                break;
            case SynapseInfo.PLAYER_LOGOUT_PACKET:
                PlayerLogoutPacket playerLogoutPacket = (PlayerLogoutPacket) pk;
                UUID uuid1;
                if(this.players.containsKey(uuid1 = playerLogoutPacket.uuid)){
                    this.players.get(uuid1).close(playerLogoutPacket.reason, true);
                    this.removePlayer(uuid1);
                }
                break;
        }
    }


}
