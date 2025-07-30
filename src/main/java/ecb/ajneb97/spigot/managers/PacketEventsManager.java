package ecb.ajneb97.spigot.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.listeners.PacketEventsListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;

public class PacketEventsManager {
    private EasyCommandBlocker plugin;

    public PacketEventsManager(EasyCommandBlocker plugin){
        this.plugin = plugin;

        if(Bukkit.getServer().getPluginManager().getPlugin("packetevents") != null
                && Bukkit.getServer().getPluginManager().getPlugin("packetevents").isEnabled()) {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
            PacketEvents.getAPI().load();
        }
    }

    public void register() {
        PacketEvents.getAPI().getEventManager().registerListener(new PacketEventsListener(PacketListenerPriority.HIGHEST, plugin));
    }

    public void terminate() {

        PacketEvents.getAPI().terminate();
    }
}
