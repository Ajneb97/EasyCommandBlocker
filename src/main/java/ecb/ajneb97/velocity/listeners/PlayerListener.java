package ecb.ajneb97.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.proxy.Player;
import ecb.ajneb97.core.model.internal.UseCommandResult;
import ecb.ajneb97.velocity.EasyCommandBlocker;
import ecb.ajneb97.velocity.api.CommandBlockedEvent;
import ecb.ajneb97.velocity.managers.CommandsManagerVelocity;
import ecb.ajneb97.velocity.utils.ActionsUtils;
import ecb.ajneb97.velocity.utils.OtherUtils;
import java.util.Collection;
import java.util.List;

public class PlayerListener {

    private EasyCommandBlocker plugin;
    public PlayerListener(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void executeCommand(CommandExecuteEvent event) {
        if(event.getCommandSource() instanceof Player){
            Player player = (Player) event.getCommandSource();
            String command = "/"+event.getCommand();

            if(player.hasPermission("easycommandblocker.bypass.commands")){
                return;
            }

            CommandsManagerVelocity commandsManager = plugin.getCommandsManager();
            UseCommandResult result = commandsManager.useCommand(command);
            if(!result.isCanUseCommand()){
                CommandBlockedEvent commandBlockedEvent = new CommandBlockedEvent(player,result.getFoundCommand(),command);
                plugin.getServer().getEventManager().fire(commandBlockedEvent).thenAccept((finalEvent) -> {
                    if(!finalEvent.getResult().isAllowed()){
                        return;
                    }

                    List<String> actions = commandsManager.getActionsForCustomCommand(result.getFoundCommand());
                    if(actions == null){
                        actions = commandsManager.getBlockCommandDefaultActions();
                    }
                    ActionsUtils.executeActions(actions,player);
                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                });
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onTab(PlayerAvailableCommandsEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("easycommandblocker.bypass.tab")){
            return;
        }

        CommandsManagerVelocity commandsManager = plugin.getCommandsManager();

        List<String> permissions = OtherUtils.getPermissions(player,commandsManager);
        List<String> commands = commandsManager.getTabCommands(permissions);

        Collection<String> proxyCommands = plugin.getServer().getCommandManager().getAliases();

        event.getRootNode().getChildren().removeIf((child -> {
            String command = child.getName().toLowerCase();
            return proxyCommands.contains(command) && !commands.contains("/"+command);
        }));
    }
}
