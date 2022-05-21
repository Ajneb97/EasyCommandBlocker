package ecb.ajneb97.spigot.managers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ViaVersionManager {

    private EasyCommandBlocker plugin;
    private boolean enabled;
    public ViaVersionManager(EasyCommandBlocker plugin){
        this.plugin = plugin;
        this.enabled = false;
        if(Bukkit.getServer().getPluginManager().getPlugin("ViaVersion") != null) {
            this.enabled = true;
        }
    }

    public boolean playerIsLegacy(Player player){
        if(!enabled){
            return false;
        }

        ViaAPI api = Via.getAPI();
        int version = api.getPlayerVersion(player);
        if(version <= 340){
            return true;
        }
        return false;
    }
}
