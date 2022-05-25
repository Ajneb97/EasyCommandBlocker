package ecb.ajneb97.spigot.utils;

import java.util.List;

import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.titleapi.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ActionsUtils {

	public static void executeAction(String action,Player player){
		if(action.startsWith("message: ")) {
			String message = action.replace("message: ","");
			player.sendMessage(MessagesUtils.getColoredMessage(message));
		}else if(action.startsWith("give_potion_effect: ")) {
			String[] sep = action.replace("give_potion_effect: ","").split(";");
			PotionEffectType potionType = PotionEffectType.getByName(sep[0]);
			int potionDuration = Integer.valueOf(sep[1]);
			int potionLevel = Integer.valueOf(sep[2])-1;
			PotionEffect effect = new PotionEffect(potionType,potionDuration,potionLevel);
			player.addPotionEffect(effect);
		}else if(action.startsWith("console_command: ")) {
			String line = action.replace("console_command: ", "").replace("%player%", player.getName());
			ConsoleCommandSender sender = Bukkit.getConsoleSender();
			Bukkit.dispatchCommand(sender, line);
		}else if(action.startsWith("title: ")) {
			String[] sep = action.replace("title: ", "").split(";");
			int fadeIn = Integer.valueOf(sep[0]);
			int stay = Integer.valueOf(sep[1]);
			int fadeOut = Integer.valueOf(sep[2]);
			String title = sep[3];
			String subtitle = sep[4];
			if(title.equals("none")) {
				title = "";
			}
			if(subtitle.equals("none")) {
				subtitle = "";
			}
			TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
		}else if(action.startsWith("playsound: ")) {
			String[] sep = action.replace("playsound: ", "").split(";");
			Sound sound = null;
			int volume = 0;
			float pitch = 0;
			try {
				sound = Sound.valueOf(sep[0]);
				volume = Integer.valueOf(sep[1]);
				pitch = Float.valueOf(sep[2]);
				player.playSound(player.getLocation(), sound, volume, pitch);
			}catch(Exception e ) {
				Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(EasyCommandBlocker.prefix+
						" &7Sound Name: &c"+sep[0]+" &7is not valid. Change it in the config!"));
			}
		}
	}

	public static void executeActions(List<String> actions,Player player) {
		for(String action : actions) {
			executeAction(action,player);
		}
	}
}
