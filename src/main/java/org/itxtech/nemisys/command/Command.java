package org.itxtech.nemisys.command;

import lombok.Getter;
import lombok.Setter;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.data.*;
import org.itxtech.nemisys.event.TranslationContainer;
import org.itxtech.nemisys.utils.TextFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Command {

    protected CommandData commandData;

    protected String description = "";
    protected String usageMessage = "";
    private String permission = null;
    private String permissionMessage = null;
    private String name;
    private String nextLabel;
    private String label;
    private String[] aliases = new String[0];
    private String[] activeAliases = new String[0];
    private CommandMap commandMap = null;

    protected Map<String, CommandParameter[]> commandParameters = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private boolean global;

    public Command(String name) {
        this(name, "", null, new String[0]);
    }

    public Command(String name, String description) {
        this(name, description, null, new String[0]);
    }

    public Command(String name, String description, String usageMessage) {
        this(name, description, usageMessage, new String[0]);
    }

    public Command(String name, String description, String usageMessage, String[] aliases) {
        this.name = name;
        this.nextLabel = name;
        this.label = name;
        this.description = description;
        this.usageMessage = usageMessage == null ? "/" + name : usageMessage;
        this.aliases = aliases;
        this.activeAliases = aliases;
        this.commandParameters.put("default", new CommandParameter[]{new CommandParameter("args", CommandParamType.RAWTEXT, true)});
    }

    /*public CommandData getCommandData() {
        if (commandData == null) {
            commandData = generateCustomCommandData();
        }

        return commandData;
    }*/

    public abstract boolean execute(CommandSender sender, String commandLabel, String[] args);

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public boolean setLabel(String name) {
        this.nextLabel = name;
        if (!this.isRegistered()) {
            this.label = name;
            return true;
        }
        return false;
    }

    public boolean register(CommandMap commandMap) {
        if (this.allowChangesFrom(commandMap)) {
            this.commandMap = commandMap;
            return true;
        }
        return false;
    }

    public boolean unregister(CommandMap commandMap) {
        if (this.allowChangesFrom(commandMap)) {
            this.commandMap = null;
            this.activeAliases = this.aliases;
            this.label = this.nextLabel;
            return true;
        }
        return false;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean testPermission(CommandSender target) {
        if (this.testPermissionSilent(target)) {
            return true;
        }

        if (this.permissionMessage == null) {
            target.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", this.name));
        } else if (!this.permissionMessage.equals("")) {
            target.sendMessage(this.permissionMessage.replace("<permission>", this.permission));
        }

        return false;
    }

    public boolean testPermissionSilent(CommandSender target) {
        if (this.permission == null || this.permission.equals("")) {
            return true;
        }

        String[] permissions = this.permission.split(";");
        for (String permission : permissions) {
            if (target.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    public CommandParameter[] getCommandParameters(String key) {
        return commandParameters.get(key);
    }

    public Map<String, CommandParameter[]> getCommandParameters() {
        return commandParameters;
    }

    public void setCommandParameters(Map<String, CommandParameter[]> commandParameters) {
        this.commandParameters = commandParameters;
    }

    public void addCommandParameters(String key, CommandParameter[] parameters) {
        this.commandParameters.put(key, parameters);
    }

    /**
     * Returns an CommandData containing command data
     *
     * @return CommandData
     */
    public CommandData getDefaultCommandData() {
        return this.commandData;
    }

    /**
     * Generates modified command data for the specified player
     * for AvailableCommandsPacket.
     *
     * @return CommandData|null
     */
    public CommandDataVersions generateCustomCommandData(Player player) {
        if (!this.testPermission(player)) {
            return null;
        }

        CommandData customData = new CommandData();

        if (getAliases().length > 0) {
            List<String> aliases = new ArrayList(Arrays.asList(getAliases()));
            if (!aliases.contains(this.name)) {
                aliases.add(this.name);
            }

            customData.aliases = new CommandEnum(this.name + "Aliases", aliases);
        }

        customData.description = player.getServer().getLanguage().translateString(this.getDescription());
        this.commandParameters.forEach((key, par) -> {
            CommandOverload overload = new CommandOverload();
            overload.input.parameters = par;
            customData.overloads.put(key, overload);
        });
        if (customData.overloads.size() == 0) customData.overloads.put("default", new CommandOverload());

        CommandDataVersions versions = new CommandDataVersions();
        versions.versions.add(customData);

        return versions;
    }

    public Map<String, CommandOverload> getOverloads() {
        return this.commandData.overloads;
    }

    public boolean allowChangesFrom(CommandMap commandMap) {
        return commandMap == null || commandMap.equals(this.commandMap);
    }

    public boolean isRegistered() {
        return this.commandMap != null;
    }

    public String[] getAliases() {
        return this.activeAliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
        if (!this.isRegistered()) {
            this.activeAliases = aliases;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usageMessage;
    }

    public void setUsage(String usageMessage) {
        this.usageMessage = usageMessage;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
