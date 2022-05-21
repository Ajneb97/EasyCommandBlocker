package ecb.ajneb97.waterfall.api;


import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class CommandBlockedEvent extends Event implements Cancellable {

    private boolean isCancelled;
    private ProxiedPlayer player;
    private String command;
    private String fullCommand;

    public CommandBlockedEvent(ProxiedPlayer player, String command, String fullCommand){
        this.player = player;
        this.command = command;
        this.fullCommand = fullCommand;
    }

    public ProxiedPlayer getPlayer() {
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
}
