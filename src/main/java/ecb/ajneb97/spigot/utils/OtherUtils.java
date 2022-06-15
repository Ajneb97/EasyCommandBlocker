package ecb.ajneb97.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OtherUtils {

    public static boolean serverIsNew() {
        if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")
                || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19")) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean serverIsLegacy() {
        if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") ||
                Bukkit.getVersion().contains("1.15") || Bukkit.getVersion().contains("1.16")
                || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")
                || Bukkit.getVersion().contains("1.19")) {
            return false;
        }else {
            return true;
        }
    }

    public static List<String> getPlayerPermissionsList(Player player){
        List<String> permissions = new ArrayList<String>();
        Set<PermissionAttachmentInfo> pai = player.getEffectivePermissions();
        for(PermissionAttachmentInfo p : pai){
            if(p.getValue()){
                permissions.add(p.getPermission());
            }
        }
        return permissions;
    }
}
