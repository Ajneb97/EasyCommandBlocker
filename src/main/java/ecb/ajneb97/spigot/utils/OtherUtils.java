package ecb.ajneb97.spigot.utils;

import org.bukkit.Bukkit;

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
}
