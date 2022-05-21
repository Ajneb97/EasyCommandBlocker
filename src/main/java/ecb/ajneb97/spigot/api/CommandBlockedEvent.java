package ecb.ajneb97.spigot.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandBlockedEvent extends Event implements Cancellable {

    private boolean isCancelled;
    private Player player;
    private String command;
    private String fullCommand;

    private static final HandlerList handlers = new HandlerList();

    public CommandBlockedEvent(Player player, String command, String fullCommand){
        this.player = player;
        this.command = command;
        this.fullCommand = fullCommand;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCommand() {
        return command;
    }

    public String getFullCommand() {
        return fullCommand;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
