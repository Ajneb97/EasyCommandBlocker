package ecb.ajneb97.spigot.listeners;

import com.comphenix.protocol.reflect.StructureModifier;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class PacketEventsListener extends PacketListenerAbstract {
    private EasyCommandBlocker plugin;
    private Map<UUID, String> commandsWaiting = new HashMap<>();

    public PacketEventsListener(PacketListenerPriority priority, EasyCommandBlocker plugin) {
        super(priority);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacketType() != PacketType.Play.Client.TAB_COMPLETE) return;
        if (e.isCancelled()) return;

        Player player = e.getPlayer();

        if((player.isOp() && plugin.getConfigManager().getConfig().getBoolean("can_ops_bypass")) || player.hasPermission("easycommandblocker.bypass.tab")){
            return;
        }

        WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(e);
        String text = wrapper.getText();

        if (text.isEmpty()) {
            return;
        }

        boolean playerIsLegacy = plugin.getViaVersionManager().playerIsLegacy(player);
        if(OtherUtils.serverIsLegacy()){
            if(playerIsLegacy){
                commandsWaiting.put(player.getUniqueId(),text);
            }else{
                e.setCancelled(true);
            }
        }else{
            if(playerIsLegacy){
                e.setCancelled(true);
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent e) {
        if (e.getPacketType() != PacketType.Play.Server.TAB_COMPLETE) return;

        Player player = e.getPlayer();
        boolean playerIsLegacy = plugin.getViaVersionManager().playerIsLegacy(player);
        if(!playerIsLegacy || !OtherUtils.serverIsLegacy()){
            return;
        }
        if((player.isOp() && plugin.getConfigManager().getConfig().getBoolean("can_ops_bypass")) || player.hasPermission("easycommandblocker.bypass.tab")){
            return;
        }

        WrapperPlayServerTabComplete wrapper = new WrapperPlayServerTabComplete(e);
        List<String> newSuggestions = new ArrayList<String>();

        String waitCommand = commandsWaiting.get(player.getUniqueId());
        commandsWaiting.remove(player.getUniqueId());
        if(waitCommand == null){
            //Empty completions
            e.setCancelled(true);
            return;
        }
        if(!waitCommand.startsWith("/")){
            //Send username completions
            return;
        }

        CommandsManager commandsManager = plugin.getCommandsManager();
        List<String> commands = commandsManager.getTabCommands(OtherUtils.getPlayerPermissionsList(player));
        if(commands == null){
            return;
        }

        boolean isArgument = false;
        if(waitCommand.contains(" ")){
            waitCommand = waitCommand.split(" ")[0];
            isArgument = true;
        }

        for(String command : commands){
            command = command.split(" ")[0];
            if(!newSuggestions.contains(command) && command.startsWith(waitCommand)){
                if(isArgument){
                    return;
                }
                newSuggestions.add(command);
            }
        }

        List<WrapperPlayServerTabComplete.CommandMatch> commandMatches = new ArrayList<>();
        for (String suggestion : newSuggestions) {
            commandMatches.add(new WrapperPlayServerTabComplete.CommandMatch(suggestion, null));
        }
        wrapper.setCommandMatches(commandMatches);

    }
}
