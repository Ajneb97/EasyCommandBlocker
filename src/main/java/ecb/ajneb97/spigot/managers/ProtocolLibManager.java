package ecb.ajneb97.spigot.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProtocolLibManager {

    private EasyCommandBlocker plugin;
    private boolean enabled;
    private HashMap<UUID, String> commandsWaiting = new HashMap<>();
    public ProtocolLibManager(EasyCommandBlocker plugin){
        this.plugin = plugin;
        this.enabled = false;
        if(Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            this.enabled = true;
            PacketAdapter packet1 = getTabClientAdapter(PacketType.Play.Client.TAB_COMPLETE);
            ProtocolLibrary.getProtocolManager().addPacketListener(packet1);
            PacketAdapter packet2 = getTabServerAdapter(PacketType.Play.Server.TAB_COMPLETE);
            ProtocolLibrary.getProtocolManager().addPacketListener(packet2);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public PacketAdapter getTabClientAdapter(PacketType type) {
        return new PacketAdapter(plugin, ListenerPriority.HIGHEST, type) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                EasyCommandBlocker pluginE = (EasyCommandBlocker) plugin;
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                if(event.isCancelled()){
                    return;
                }
                if(player.isOp() || player.hasPermission("easycommandblocker.bypass.tab")){
                    return;
                }
                String message = (packet.getSpecificModifier(String.class).read(0));
                if(message.isEmpty()){
                    return;
                }

                boolean playerIsLegacy = pluginE.getViaVersionManager().playerIsLegacy(player);
                if(OtherUtils.serverIsLegacy()){
                    if(playerIsLegacy){
                        commandsWaiting.put(player.getUniqueId(),message);
                    }else{
                        event.setCancelled(true);
                    }
                }else{
                    if(playerIsLegacy){
                        event.setCancelled(true);
                    }
                }
            }
        };
    }

    public PacketAdapter getTabServerAdapter(PacketType type) {
        return new PacketAdapter(plugin, ListenerPriority.HIGHEST, type) {
            @Override
            public void onPacketSending(PacketEvent event) {
                EasyCommandBlocker pluginE = (EasyCommandBlocker) plugin;
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                boolean playerIsLegacy = pluginE.getViaVersionManager().playerIsLegacy(player);
                if(!playerIsLegacy || !OtherUtils.serverIsLegacy()){
                    return;
                }
                if(player.isOp() || player.hasPermission("easycommandblocker.bypass.tab")){
                    return;
                }

                StructureModifier<String[]> structureModifier = packet.getSpecificModifier(String[].class);
                List<String> newSuggestions = new ArrayList<String>();

                String waitCommand = commandsWaiting.get(player.getUniqueId());
                commandsWaiting.remove(player.getUniqueId());
                if(waitCommand == null){
                    //Empty completions
                    event.setCancelled(true);
                    return;
                }
                if(!waitCommand.startsWith("/")){
                    //Send username completions
                    return;
                }

                CommandsManager commandsManager = pluginE.getCommandsManager();
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

                structureModifier.write(0,newSuggestions.toArray(new String[0]));
            }
        };
    }
}
