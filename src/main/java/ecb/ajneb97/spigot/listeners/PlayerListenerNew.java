package ecb.ajneb97.spigot.listeners;

import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.List;

public class PlayerListenerNew implements Listener {
    private EasyCommandBlocker plugin;
    public PlayerListenerNew(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event){
        Player player = event.getPlayer();

        if(player.isOp() || player.hasPermission("easycommandblocker.bypass.tab")){
            return;
        }
        CommandsManager commandsManager = plugin.getCommandsManager();
        List<String> commands = commandsManager.getTabCommands(OtherUtils.getPlayerPermissionsList(player));

        event.getCommands().clear();

        if(commands == null){
            return;
        }
        for(String command : commands){
            command = command.replaceFirst("/","");
            command = command.split(" ")[0];
            if(!event.getCommands().contains(command)){
                event.getCommands().add(command);
            }
        }
    }
}
