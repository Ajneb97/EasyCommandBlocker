package ecb.ajneb97.velocity.api;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;

public class CommandBlockedEvent implements ResultedEvent<ResultedEvent.GenericResult>{
    private Player player;
    private String command;
    private String fullCommand;
    private GenericResult result = GenericResult.allowed();

    public CommandBlockedEvent(Player player, String command, String fullCommand) {
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
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = result;
    }
}
