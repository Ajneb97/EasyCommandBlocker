package ecb.ajneb97.spigot.listeners;

import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.model.internal.UseCommandResult;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.api.CommandBlockedEvent;
import ecb.ajneb97.spigot.utils.ActionsUtils;
import ecb.ajneb97.spigot.utils.MessagesUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerListener implements Listener {

    private EasyCommandBlocker plugin;
    public PlayerListener(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void executeCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        String command = event.getMessage();
        if(player.isOp() || player.hasPermission("easycommandblocker.bypass.commands")){
            return;
        }

        CommandsManager commandsManager = plugin.getCommandsManager();
        UseCommandResult result = commandsManager.useCommand(command);
        if(!result.isCanUseCommand()){
            CommandBlockedEvent commandBlockedEvent = new CommandBlockedEvent(player,result.getFoundCommand(),command);
            plugin.getServer().getPluginManager().callEvent(commandBlockedEvent);

            if(!commandBlockedEvent.isCancelled()){
                List<String> actions = commandsManager.getActionsForCustomCommand(result.getFoundCommand());
                if(actions == null){
                    actions = commandsManager.getBlockCommandDefaultActions();
                }
                ActionsUtils.executeActions(actions,player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void updateNotification(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String latestVersion = plugin.getUpdateCheckerManager().getLatestVersion();
        if(player.isOp() && !(plugin.version.equals(latestVersion))){
            player.sendMessage(MessagesUtils.getColoredMessage(plugin.prefix+" &cThere is a new version available. &e(&7"+latestVersion+"&e)"));
            player.sendMessage(MessagesUtils.getColoredMessage("&cYou can download it at: &ahttps://www.spigotmc.org/resources/101752/"));
        }
    }
}
