package ecb.ajneb97.spigot.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProtocolLibManager {

    private EasyCommandBlocker plugin;
    private boolean enabled;
    public ProtocolLibManager(EasyCommandBlocker plugin){
        this.plugin = plugin;
        this.enabled = false;
        if(Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            this.enabled = true;
            PacketAdapter packet1 = getTabClientAdapter(PacketType.Play.Client.TAB_COMPLETE);
            ProtocolLibrary.getProtocolManager().addPacketListener(packet1);
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

                if(player.isOp() || player.hasPermission("easycommandblocker.bypass.tab")){
                    return;
                }
                String message = (packet.getSpecificModifier(String.class).read(0)).split(" ")[0];

                if(OtherUtils.serverIsLegacy() || pluginE.getViaVersionManager().playerIsLegacy(player)){
                    if(!message.startsWith("/")){
                        return;
                    }
                    event.setCancelled(true);
                }
            }
        };
    }
}
