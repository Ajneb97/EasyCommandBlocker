package ecb.ajneb97.waterfall.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import ecb.ajneb97.core.model.GlobalVariables;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PluginMessagingUtils {

    public static void sendMessage(ProxiedPlayer player, String subChannel, String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(data);

        player.getServer().getInfo().sendData(GlobalVariables.bungeeMainChannel, out.toByteArray() );
    }
}
