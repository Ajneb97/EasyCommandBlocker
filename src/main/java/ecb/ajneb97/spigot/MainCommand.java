package ecb.ajneb97.spigot;




import ecb.ajneb97.spigot.utils.MessagesUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {

	private EasyCommandBlocker plugin;
	public MainCommand(EasyCommandBlocker plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.isOp() && !sender.hasPermission("easycommandblocker.admin")){
			return false;
		}

		if(args.length >= 1){
			if(args[0].equalsIgnoreCase("reload")){
				reload(sender);
			}
		}

		return true;

	}

	public void reload(CommandSender sender){
		plugin.customReload();
		sender.sendMessage(MessagesUtils.getColoredMessage(plugin.prefix+" &aConfig reloaded!"));
	}
}
