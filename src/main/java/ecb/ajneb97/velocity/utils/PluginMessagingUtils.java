package ecb.ajneb97.velocity.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ecb.ajneb97.core.model.GlobalVariables;

public class PluginMessagingUtils {

    public static void sendMessage(Player player, String subChannel, String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(data);

        player.getCurrentServer().ifPresent((ServerConnection serverConnection) -> {
            MinecraftChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.from(GlobalVariables.bungeeMainChannel);
            serverConnection.sendPluginMessage(channelIdentifier,out.toByteArray());
        });
    }
}
