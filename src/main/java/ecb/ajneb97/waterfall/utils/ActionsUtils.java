package ecb.ajneb97.waterfall.utils;


import ecb.ajneb97.core.model.GlobalVariables;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;


public class ActionsUtils {

	public static void executeActions(List<String> actions, ProxiedPlayer player) {
		for(String action : actions) {
			if(action.startsWith("message: ")) {
				String message = action.replace("message: ","");
				player.sendMessage(MessagesUtils.getColoredMessage(message));
			}else{
				PluginMessagingUtils.sendMessage(player, GlobalVariables.bungeeActionsSubChannel,action);
			}
		}
	}
}
