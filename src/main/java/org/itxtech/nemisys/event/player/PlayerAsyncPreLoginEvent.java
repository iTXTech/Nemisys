package org.itxtech.nemisys.event.player;

import lombok.Getter;
import lombok.Setter;
import org.itxtech.nemisys.event.HandlerList;

import java.util.UUID;

/**
 * This event is called asynchronously
 *
 * @author CreeperFace
 */
public class PlayerAsyncPreLoginEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Getter
    private final String name;
    @Getter
    private final UUID uuid;
    @Getter
    private final String address;
    @Getter
    private final int port;

    @Getter
    @Setter
    private LoginResult loginResult = LoginResult.SUCCESS;
    @Getter
    @Setter
    private String kickMessage = "Plugin Reason";

    public PlayerAsyncPreLoginEvent(String name, UUID uuid, String address, int port) {
        super(null);
        this.name = name;
        this.uuid = uuid;
        this.address = address;
        this.port = port;
    }

    public void allow() {
        this.loginResult = LoginResult.SUCCESS;
    }

    public void disAllow(String message) {
        this.loginResult = LoginResult.KICK;
        this.kickMessage = message;
    }

    public enum LoginResult {
        SUCCESS,
        KICK
    }
}
