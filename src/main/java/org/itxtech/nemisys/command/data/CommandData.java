package org.itxtech.nemisys.command.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CreeperFace
 */
public class CommandData implements Cloneable {

    public CommandEnum aliases;
    public String description = "description";
    public Map<String, CommandOverload> overloads = new HashMap<>();
    public int permission;
    public int flags;

    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }
}
