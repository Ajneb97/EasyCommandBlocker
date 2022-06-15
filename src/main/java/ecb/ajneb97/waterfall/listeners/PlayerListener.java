package ecb.ajneb97.waterfall.listeners;

import ecb.ajneb97.core.model.internal.UseCommandResult;
import ecb.ajneb97.waterfall.EasyCommandBlocker;
import ecb.ajneb97.waterfall.api.CommandBlockedEvent;
import ecb.ajneb97.waterfall.managers.CommandsManagerWaterfall;
import ecb.ajneb97.waterfall.utils.ActionsUtils;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {

    private EasyCommandBlocker plugin;
    public PlayerListener(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void executeCommand(ChatEvent event){
        if(event.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            String command = event.getMessage();

            if(!command.startsWith("/")){
                return;
            }
            if(player.hasPermission("easycommandblocker.bypass.commands")){
                return;
            }

            CommandsManagerWaterfall commandsManager = plugin.getCommandsManager();
            UseCommandResult result = commandsManager.useCommand(command);
            if(!result.isCanUseCommand()){
                CommandBlockedEvent commandBlockedEvent = new CommandBlockedEvent(player,result.getFoundCommand(),command);
                plugin.getProxy().getPluginManager().callEvent(commandBlockedEvent);

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
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTab(ProxyDefineCommandsEvent event){
        if(event.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

            if(player.hasPermission("easycommandblocker.bypass.tab")){
                return;
            }

            CommandsManagerWaterfall commandsManager = plugin.getCommandsManager();
            List<String> permissions = new ArrayList<String>(player.getPermissions());
            List<String> commands = commandsManager.getTabCommands(permissions);

            Map<String, Command> serverCommands = event.getCommands();

            Iterator<Map.Entry<String,Command>> iter = serverCommands.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String,Command> entry = iter.next();
                if(!commands.contains("/"+entry.getKey().toLowerCase())){
                    iter.remove();
                }
            }
        }
    }
}
