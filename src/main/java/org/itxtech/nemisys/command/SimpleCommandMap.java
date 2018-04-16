package org.itxtech.nemisys.command;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.command.defaults.*;
import org.itxtech.nemisys.event.TranslationContainer;
import org.itxtech.nemisys.utils.MainLogger;
import org.itxtech.nemisys.utils.TextFormat;
import org.itxtech.nemisys.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SimpleCommandMap implements CommandMap {
    protected Map<String, Command> knownCommands = new ConcurrentHashMap<>();

    private Server server;

    public SimpleCommandMap(Server server) {
        this.server = server;
        this.setDefaultCommands();
    }

    private void setDefaultCommands() {
        this.register("nukkit", new VersionCommand("version"));
        this.register("nukkit", new PluginsCommand("plugins"));
        this.register("nukkit", new HelpCommand("help"));
        this.register("nukkit", new StopCommand("stop"));
        this.register("nukkit", new ListCommand("list"));
        this.register("nukkit", new KickCommand("kick"));
        this.register("nukkit", new StatusCommand("status"));
        this.register("nukkit", new GarbageCollectorCommand("gc"));
        this.register("nukkit", new ServerCommand("server"));
    }

    @Override
    public void registerAll(String fallbackPrefix, List<? extends Command> commands) {
        for (Command command : commands) {
            this.register(fallbackPrefix, command);
        }
    }

    @Override
    public boolean register(String fallbackPrefix, Command command) {
        return this.register(fallbackPrefix, command, null);
    }

    @Override
    public boolean register(String fallbackPrefix, Command command, String label) {
        if (label == null) {
            label = command.getName();
        }
        label = label.trim().toLowerCase();
        fallbackPrefix = fallbackPrefix.trim().toLowerCase();

        boolean registered = this.registerAlias(command, false, fallbackPrefix, label);

        List<String> aliases = new ArrayList<>(Arrays.asList(command.getAliases()));

        for (Iterator<String> iterator = aliases.iterator(); iterator.hasNext(); ) {
            String alias = iterator.next();
            if (!this.registerAlias(command, true, fallbackPrefix, alias)) {
                iterator.remove();
            }
        }
        command.setAliases(aliases.stream().toArray(String[]::new));

        if (!registered) {
            command.setLabel(fallbackPrefix + ":" + label);
        }

        command.register(this);

        return registered;
    }

    private boolean registerAlias(Command command, boolean isAlias, String fallbackPrefix, String label) {
        this.knownCommands.put(fallbackPrefix + ":" + label, command);

        //if you're registering a command alias that is already registered, then return false
        boolean alreadyRegistered = this.knownCommands.containsKey(label);
        Command existingCommand = this.knownCommands.get(label);
        boolean existingCommandIsNotVanilla = alreadyRegistered && !(existingCommand instanceof VanillaCommand);
        //basically, if we're an alias and it's already registered, or we're a vanilla command, then we can't override it
        if ((command instanceof VanillaCommand || isAlias) && alreadyRegistered && existingCommandIsNotVanilla) {
            return false;
        }

        //if you're registering a name (alias or label) which is identical to another command who's primary name is the same
        //so basically we can't override the main name of a command, but we can override aliases if we're not an alias

        //added the last statement which will allow us to override a VanillaCommand unconditionally
        if (alreadyRegistered && existingCommand.getLabel() != null && existingCommand.getLabel().equals(label) && existingCommandIsNotVanilla) {
            return false;
        }

        //you can now assume that the command is either uniquely named, or overriding another command's alias (and is not itself, an alias)

        if (!isAlias) {
            command.setLabel(label);
        }

        this.knownCommands.put(label, command);

        return true;
    }

    @Override
    public boolean dispatch(CommandSender sender, String cmdLine) {
        String[] args = cmdLine.split(" ");

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase();
        String[] newargs = new String[args.length - 1];
        System.arraycopy(args, 1, newargs, 0, newargs.length);
        args = newargs;
        Command target = this.getCommand(sentCommandLabel);

        if (target == null || (sender instanceof Player && !target.isGlobal())) {
            return false;
        }

        try {
            target.execute(sender, sentCommandLabel, args);
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.exception"));
            this.server.getLogger().critical(this.server.getLanguage().translateString("nemisys.command.exception", new String[]{cmdLine, target.toString(), Utils.getExceptionMessage(e)}));
            MainLogger logger = sender.getServer().getLogger();
            if (logger != null) {
                logger.logException(e);
            }
        }

        return true;
    }

    @Override
    public void clearCommands() {
        for (Command command : this.knownCommands.values()) {
            command.unregister(this);
        }
        this.knownCommands.clear();
        this.setDefaultCommands();
    }

    @Override
    public Command getCommand(String name) {
        return this.knownCommands.get(name);
    }

    public Map<String, Command> getCommands() {
        return knownCommands;
    }

}
