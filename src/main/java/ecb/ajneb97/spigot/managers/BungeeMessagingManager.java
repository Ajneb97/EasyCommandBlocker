package ecb.ajneb97.spigot.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import ecb.ajneb97.core.model.GlobalVariables;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.utils.ActionsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeMessagingManager implements PluginMessageListener {

    private EasyCommandBlocker plugin;
    public BungeeMessagingManager(EasyCommandBlocker plugin){
        if (!Bukkit.getServer().spigot().getConfig().getBoolean( "settings.bungeecord" ) ) {
            return;
        }

        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, GlobalVariables.bungeeMainChannel, this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equalsIgnoreCase(GlobalVariables.bungeeMainChannel)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if(subChannel.equalsIgnoreCase( GlobalVariables.bungeeActionsSubChannel)) {
            String data = in.readUTF();

            ActionsUtils.executeAction(data,player);
        }
    }
}
