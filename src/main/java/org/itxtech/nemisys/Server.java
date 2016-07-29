package org.itxtech.nemisys;

import com.google.gson.Gson;
import org.itxtech.nemisys.command.*;
import org.itxtech.nemisys.event.*;
import org.itxtech.nemisys.event.server.QueryRegenerateEvent;
import org.itxtech.nemisys.lang.BaseLang;
import org.itxtech.nemisys.math.NemisysMath;
import org.itxtech.nemisys.network.Network;
import org.itxtech.nemisys.network.RakNetInterface;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.network.SynapseInterface;
import org.itxtech.nemisys.network.query.QueryHandler;
import org.itxtech.nemisys.network.rcon.RCON;
import org.itxtech.nemisys.plugin.JavaPluginLoader;
import org.itxtech.nemisys.plugin.Plugin;
import org.itxtech.nemisys.plugin.PluginLoadOrder;
import org.itxtech.nemisys.plugin.PluginManager;
import org.itxtech.nemisys.scheduler.ServerScheduler;
import org.itxtech.nemisys.utils.*;
import sun.security.provider.MD5;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * author: MagicDroidX & Box
 * Nukkit
 */
public class Server {

    private static Server instance = null;

    private boolean isRunning = true;
    private boolean hasStopped = false;

    private PluginManager pluginManager = null;
    private ServerScheduler scheduler = null;
    private int tickCounter;
    private long nextTick;
    private float[] tickAverage = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
    private float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float maxTick = 100;
    private float maxUse = 0;

    private MainLogger logger;

    private CommandReader console;
    private SimpleCommandMap commandMap;
    private ConsoleCommandSender consoleSender;

    private int maxPlayers;
    private RCON rcon;
    private Network network;
    private BaseLang baseLang;

    private boolean forceLanguage = false;

    private UUID serverID;

    private String filePath;
    private String dataPath;
    private String pluginPath;

    private QueryHandler queryHandler;

    private QueryRegenerateEvent queryRegenerateEvent;

    private Config properties;

    private Map<String, Player> players = new HashMap<>();
    private Map<Integer, String> identifier = new HashMap<>();

    private SynapseInterface synapseInterface;
    private Map<String, Client> clients = new HashMap<>();
    private ClientData clientData = new ClientData();
    private String clientDataJson = "";
    private Map<String, Client> mainClients = new HashMap<>();

    public Server(MainLogger logger, final String filePath, String dataPath, String pluginPath) {
        instance = this;
        this.logger = logger;

        this.filePath = filePath;

        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }

        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";

        this.console = new CommandReader();
        this.console.start();

        this.logger.info("Loading " + TextFormat.GREEN + "server properties" + TextFormat.WHITE + "...");
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
            {
                put("motd", "Nemisys Proxy: Minecraft PE Server");
                put("server-port", 19132);
                put("synapse-port", 10305);
                put("password", "1234567890123456"/* TODO MD5 Password*/);
                put("lang", "eng");
                put("async-workers", "auto");
                put("enable-profiling", false);
                put("profile-report-trigger", 20);
                put("server-ip", "0.0.0.0");
                put("max-players", 20);
                put("dynamic-player-count", false);
                put("enable-query", true);
                put("enable-rcon", false);
                put("rcon.password", Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13));
                put("debug", 1);
            }
        });

        this.baseLang = new BaseLang((String) this.getConfig("lang", BaseLang.FALLBACK_LANGUAGE));
        this.logger.info(this.getLanguage().translateString("language.selected", new String[]{getLanguage().getName(), getLanguage().getLang()}));
        this.logger.info(getLanguage().translateString("nemisys.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.WHITE));

        Object poolSize = this.getConfig("async-workers", "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors() + 1, 4);
            }
        }

        ServerScheduler.WORKERS = (int) poolSize;

        this.scheduler = new ServerScheduler();

        if (this.getPropertyBoolean("enable-rcon", false)) {
            this.rcon = new RCON(this, this.getPropertyString("rcon.password", ""), (!this.getIp().equals("")) ? this.getIp() : "0.0.0.0", this.getPropertyInt("rcon.port", this.getPort()));
        }

        this.maxPlayers = this.getPropertyInt("max-players", 20);

        Nemisys.DEBUG = this.getPropertyInt("debug", 1);
        if (this.logger instanceof MainLogger) {
            this.logger.setLogDebug(Nemisys.DEBUG > 1);
        }

        this.logger.info(this.getLanguage().translateString("nemisys.server.networkStart", new String[]{this.getIp().equals("") ? "*" : this.getIp(), String.valueOf(this.getPort())}));
        this.serverID = UUID.randomUUID();

        this.network = new Network(this);
        this.network.setName(this.getMotd());

        this.logger.info(this.getLanguage().translateString("nemisys.server.info", new String[]{this.getName(), TextFormat.YELLOW + this.getNemisysVersion() + TextFormat.WHITE, TextFormat.AQUA + this.getCodename() + TextFormat.WHITE, this.getApiVersion()}));
        this.logger.info(this.getLanguage().translateString("nemisys.server.license", this.getName()));


        this.consoleSender = new ConsoleCommandSender();
        this.commandMap = new SimpleCommandMap(this);

        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.registerInterface(JavaPluginLoader.class);
        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);
        this.network.registerInterface(new RakNetInterface(this));

        this.synapseInterface = new SynapseInterface(this, this.getIp(), this.getSynapsePort());

        this.pluginManager.loadPlugins(this.pluginPath);
        this.enablePlugins(PluginLoadOrder.STARTUP);

        this.properties.save(true);

        this.start();
    }

    public void addClient(Client client){
        this.clients.put(client.getHash(), client);
        if(client.isMainServer()){
            this.mainClients.put(client.getHash(), client);
        }
    }

    public Map<String, Client> getMainClients(){
        return this.mainClients;
    }

    public void removeClient(Client client){
        if(this.clients.containsKey(client.getHash())){
            this.mainClients.remove(client.getHash());
            this.clients.remove(client.getHash());
        }
    }

    public Map<String, Client> getClients(){
        return this.clients;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public String getClientDataJson() {
        return clientDataJson;
    }

    public void updateClientData(){
        if(this.clients.size() > 0){
            this.clientData = new ClientData();
            for (Client client: this.clients.values()){
                ClientData.Entry entry = this.clientData.new Entry(client.getIp(), client.getPort(), client.getPlayers().size(),
                        client.getMaxPlayers(), client.getDescription(), client.getTicksPerSecond(), client.getTickUsage(), client.getUpTime());
                this.clientData.clientList.put(client.getHash(), entry);
            }
            this.clientDataJson = new Gson().toJson(this.clientData);
        }
    }

    public int getSynapsePort(){
        return this.getPropertyInt("synapse-port", 10305);
    }

    public boolean comparePassword(String pass){
        String truePass = this.getPropertyString("password", "1234567890123456");
        return (truePass.equals(pass));
    }

    public void enablePlugins(PluginLoadOrder type) {
        for (Plugin plugin : this.pluginManager.getPlugins().values()) {
            if (!plugin.isEnabled() && type == plugin.getDescription().getOrder()) {
                this.enablePlugin(plugin);
            }
        }
    }

    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    public void disablePlugins() {
        this.pluginManager.disablePlugins();
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) throws ServerException {
        if (sender == null) {
            throw new ServerException("CommandSender is not valid");
        }

        if (this.commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.notFound"));

        return false;
    }

    //todo: use ticker to check console
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    public void shutdown() {
        if (this.isRunning) {
            ServerKiller killer = new ServerKiller(90);
            killer.start();
        }
        this.isRunning = false;
    }

    public void forceShutdown() {
        if (this.hasStopped) {
            return;
        }

        try {

            // clean shutdown of console thread asap
            this.console.shutdown();

            this.hasStopped = true;

            this.shutdown();


            for(Client client : new ArrayList<>(this.clients.values())){
                for (Player player : new ArrayList<>(client.getPlayers().values())) {
                    player.close((String) this.getConfig("settings.shutdown-message", "Server closed"));
                }
                client.close("Synapse server closed");
            }

            if (this.rcon != null) {
                this.rcon.close();
            }

            this.getLogger().debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            this.getLogger().debug("Removing event handlers");
            HandlerList.unregisterAll();

            this.getLogger().debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            this.getLogger().debug("Closing console");
            this.console.interrupt();

            this.getLogger().debug("Stopping network interfaces");
            for (SourceInterface interfaz : new ArrayList<>(this.network.getInterfaces())) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }
            this.synapseInterface.getInterface().shutdown();

            //todo other things
        } catch (Exception e) {
            this.logger.logException(e); //todo remove this?
            this.logger.emergency("Exception happened while shutting down, exit the process");
            System.exit(1);
        }
    }

    public void start() {
        if (this.getPropertyBoolean("enable-query", true)) {
            this.queryHandler = new QueryHandler();
        }

        this.tickCounter = 0;

        this.getLogger().info(this.getLanguage().translateString("nemisys.server.startFinished", String.valueOf(Math.round(System.currentTimeMillis() - Nemisys.START_TIME))));

        this.tickProcessor();
        this.forceShutdown();
    }

    public void handlePacket(String address, int port, byte[] payload) {
        try {
            if (payload.length > 2 && Arrays.equals(Binary.subBytes(payload, 0, 2), new byte[]{(byte) 0xfe, (byte) 0xfd}) && this.queryHandler != null) {
                this.queryHandler.handle(address, port, payload);
            }
        } catch (Exception e) {
            this.logger.logException(e);

            this.getNetwork().blockAddress(address, 600);
        }
    }

    public void tickProcessor() {
        this.nextTick = System.currentTimeMillis();
        while (this.isRunning) {
            try {
                this.tick();
            } catch (RuntimeException e) {
                this.getLogger().logException(e);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
    }

    public void addPlayer(String identifier, Player player) {
        this.players.put(identifier, player);
        this.identifier.put(player.rawHashCode(), identifier);
    }

    private boolean tick() {
        long tickTime = System.currentTimeMillis();
        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -5) {
            return false;
        }

        ++this.tickCounter;

        this.network.processInterfaces();
        this.synapseInterface.process();

        if (this.rcon != null) {
            this.rcon.check();
        }

        this.scheduler.mainThreadHeartbeat(this.tickCounter);

        for (Player player : new ArrayList<>(this.players.values())) {
            player.onUpdate(this.tickCounter);
        }

        for (Client client : new ArrayList<>(this.clients.values())) {
            client.onUpdate(this.tickCounter);
        }

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.maxTick = 100;
            this.maxUse = 0;

            if ((this.tickCounter & 0b111111111) == 0) {
                try {
                    this.getPluginManager().callEvent(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                    if (this.queryHandler != null) {
                        this.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    this.logger.logException(e);
                }
            }

            this.getNetwork().updateName();
        }

        //long now = System.currentTimeMillis();
        long nowNano = System.nanoTime();
        //float tick = Math.min(100, 1000 / Math.max(1, now - tickTime));
        //float use = Math.min(1, (now - tickTime) / 50);

        float tick = (float) Math.min(100, 1000000000 / Math.max(1000000, ((double) nowNano - tickTimeNano)));
        float use = (float) Math.min(1, ((double) (nowNano - tickTimeNano)) / 50000000);

        if (this.maxTick > tick) {
            this.maxTick = tick;
        }

        if (this.maxUse < use) {
            this.maxUse = use;
        }

        System.arraycopy(this.tickAverage, 1, this.tickAverage, 0, this.tickAverage.length - 1);
        this.tickAverage[this.tickAverage.length - 1] = tick;

        System.arraycopy(this.useAverage, 1, this.useAverage, 0, this.useAverage.length - 1);
        this.useAverage[this.useAverage.length - 1] = use;

        if ((this.nextTick - tickTime) < -1000) {
            this.nextTick = tickTime;
        } else {
            this.nextTick += 10;
        }

        return true;
    }

    public void titleTick() {
        if (!Nemisys.ANSI) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        double used = NemisysMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double max = NemisysMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        String usage = Math.round(used / max * 100) + "%";
        String title = (char) 0x1b + "]0;" + this.getName() + " " +
                this.getNemisysVersion() +
                " | Online " + this.players.size() + "/" + this.getMaxPlayers() +
                " | Clients " + this.clients.size() +
                " | Memory " + usage;
        if (!Nemisys.shortTitle) {
            title += " | U " + NemisysMath.round((this.network.getUpload() / 1024 * 1000), 2)
                    + " D " + NemisysMath.round((this.network.getDownload() / 1024 * 1000), 2) + " kB/s";
        }
        if (this.synapseInterface.getInterface().getSessionManager() != null) {
            title += " | SynLibTPS " + this.synapseInterface.getInterface().getSessionManager().getTicksPerSecond() +
                    " | SynLibLoad " + this.synapseInterface.getInterface().getSessionManager().getTickUsage() + "%";
        }

        title += " | TPS " + this.getTicksPerSecond() +
                " | Load " + this.getTickUsage() + "%" + (char) 0x07;

        System.out.print(title);

        this.network.resetStatistics();
    }

    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
    }

    public String getName() {
        return "Nemisys";
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getNemisysVersion() {
        return Nemisys.VERSION;
    }

    public String getCodename() {
        return Nemisys.CODENAME;
    }

    public String getVersion() {
        return Nemisys.MINECRAFT_VERSION;
    }

    public String getApiVersion() {
        return Nemisys.API_VERSION;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPort() {
        return this.getPropertyInt("server-port", 19132);
    }

    public String getIp() {
        return this.getPropertyString("server-ip", "0.0.0.0");
    }

    public UUID getServerUniqueId() {
        return this.serverID;
    }

    public String getMotd() {
        return this.getPropertyString("motd", "Nemisys Server");
    }

    public MainLogger getLogger() {
        return this.logger;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public ServerScheduler getScheduler() {
        return scheduler;
    }

    public int getTick() {
        return tickCounter;
    }

    public float getTicksPerSecond() {
        return ((float) Math.round(this.maxTick * 100)) / 100;
    }

    public float getTicksPerSecondAverage() {
        float sum = 0;
        int count = this.tickAverage.length;
        for (float aTickAverage : this.tickAverage) {
            sum += aTickAverage;
        }
        return (float) NemisysMath.round(sum / count, 2);
    }

    public float getTickUsage() {
        return (float) NemisysMath.round(this.maxUse * 100, 2);
    }

    public float getTickUsageAverage() {
        float sum = 0;
        int count = this.useAverage.length;
        for (float aUseAverage : this.useAverage) {
            sum += aUseAverage;
        }
        return ((float) Math.round(sum / count * 100)) / 100;
    }

    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    public Map<String, Player> getOnlinePlayers() {
        return this.players;
    }

    public Player getPlayer(String name) {
        Player found = null;
        name = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().startsWith(name)) {
                int curDelta = player.getName().length() - name.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) {
                    break;
                }
            }
        }

        return found;
    }

    public Player getPlayerExact(String name) {
        name = name.toLowerCase();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(name)) {
                return player;
            }
        }

        return null;
    }

    public Player[] matchPlayer(String partialName) {
        partialName = partialName.toLowerCase();
        List<Player> matchedPlayer = new ArrayList<>();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(partialName)) {
                return new Player[]{player};
            } else if (player.getName().toLowerCase().contains(partialName)) {
                matchedPlayer.add(player);
            }
        }

        return matchedPlayer.toArray(new Player[matchedPlayer.size()]);
    }

    public void removePlayer(Player player) {
        if (this.identifier.containsKey(player.rawHashCode())) {
            String identifier = this.identifier.get(player.rawHashCode());
            this.players.remove(identifier);
            this.identifier.remove(player.rawHashCode());
            return;
        }

        for (String identifier : new ArrayList<>(this.players.keySet())) {
            Player p = this.players.get(identifier);
            if (player == p) {
                this.players.remove(identifier);
                this.identifier.remove(player.rawHashCode());
                break;
            }
        }
    }

    public BaseLang getLanguage() {
        return baseLang;
    }

    public boolean isLanguageForced() {
        return forceLanguage;
    }

    public Network getNetwork() {
        return network;
    }

    public Object getConfig(String variable) {
        return this.getConfig(variable, null);
    }

    public Object getConfig(String variable, Object defaultValue) {
        Object value = this.properties.get(variable);
        return value == null ? defaultValue : value;
    }

    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    public Object getProperty(String variable, Object defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
    }

    public void setPropertyString(String variable, String value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public String getPropertyString(String variable) {
        return this.getPropertyString(variable, null);
    }

    public String getPropertyString(String variable, String defaultValue) {
        return this.properties.exists(variable) ? (String) this.properties.get(variable) : defaultValue;
    }

    public int getPropertyInt(String variable) {
        return this.getPropertyInt(variable, null);
    }

    public int getPropertyInt(String variable, Integer defaultValue) {
        return this.properties.exists(variable) ? (!this.properties.get(variable).equals("") ? Integer.parseInt(String.valueOf(this.properties.get(variable))) : defaultValue) : defaultValue;
    }

    public void setPropertyInt(String variable, int value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public boolean getPropertyBoolean(String variable) {
        return this.getPropertyBoolean(variable, null);
    }

    public boolean getPropertyBoolean(String variable, Object defaultValue) {
        Object value = this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        switch (String.valueOf(value)) {
            case "on":
            case "true":
            case "1":
            case "yes":
                return true;
        }
        return false;
    }

    public void setPropertyBoolean(String variable, boolean value) {
        this.properties.set(variable, value ? "1" : "0");
        this.properties.save();
    }

    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) command;
        } else {
            return null;
        }
    }

    public static Server getInstance() {
        return instance;
    }

}
