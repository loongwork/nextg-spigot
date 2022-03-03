package net.loongwork.nextg.spigot.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SocketMessageEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final String message;

    public SocketMessageEvent(String message) {
        super(true);
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
