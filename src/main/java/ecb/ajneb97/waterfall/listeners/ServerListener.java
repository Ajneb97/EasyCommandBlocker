package ecb.ajneb97.waterfall.listeners;

import ecb.ajneb97.core.model.GlobalVariables;
import ecb.ajneb97.waterfall.EasyCommandBlocker;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerListener implements Listener {

    private EasyCommandBlocker plugin;
    public ServerListener(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event){
        // Check if the identifier matches first, no matter the source.
        if (!event.getTag().equals(GlobalVariables.bungeeMainChannel)) {
            return;
        }

        event.setCancelled(true);
    }
}
