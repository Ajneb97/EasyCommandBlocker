package ecb.ajneb97.velocity.utils;


import com.velocitypowered.api.proxy.Player;
import ecb.ajneb97.core.model.GlobalVariables;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;


public class ActionsUtils {

	public static void executeActions(List<String> actions, Player player) {
		for(String action : actions) {
			if(action.startsWith("message: ")) {
				String message = action.replace("message: ","");
				player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
			}else{
				PluginMessagingUtils.sendMessage(player, GlobalVariables.bungeeActionsSubChannel, action);
			}
		}
	}
}
