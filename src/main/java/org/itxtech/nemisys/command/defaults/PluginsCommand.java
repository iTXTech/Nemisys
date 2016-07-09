package org.itxtech.nemisys.command.defaults;

import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.TranslationContainer;
import org.itxtech.nemisys.plugin.Plugin;
import org.itxtech.nemisys.utils.TextFormat;

import java.util.Map;

/**
 * Created on 2015/11/12 by xtypr.
 * Package org.itxtech.nemisys.command.defaults in project Nukkit .
 */
public class PluginsCommand extends VanillaCommand {

    public PluginsCommand(String name) {
        super(name,
                "%nemisys.command.plugins.description",
                "%nemisys.command.plugins.usage",
                new String[]{"pl"}
        );
        this.setPermission("nemisys.command.plugins");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        this.sendPluginList(sender);
        return true;
    }

    private void sendPluginList(CommandSender sender) {
        String list = "";
        Map<String, Plugin> plugins = sender.getServer().getPluginManager().getPlugins();
        for (Plugin plugin : plugins.values()) {
            if (list.length() > 0) {
                list += TextFormat.WHITE + ", ";
            }
            list += plugin.isEnabled() ? TextFormat.GREEN : TextFormat.RED;
            list += plugin.getDescription().getFullName();
        }

        sender.sendMessage(new TranslationContainer("nemisys.command.plugins.success", new String[]{String.valueOf(plugins.size()), list}));
    }
}
