package ecb.ajneb97.waterfall;


import ecb.ajneb97.waterfall.utils.MessagesUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MainCommand extends Command {

	private EasyCommandBlocker plugin;
	public MainCommand(EasyCommandBlocker plugin){
		super("ecb");
        this.plugin = plugin;
	}

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission("easycommandblocker.admin")){
            if(args.length >= 1){
                if(args[0].equalsIgnoreCase("reload")){
                    reload(sender);
                }
            }
        }
    }

	public void reload(CommandSender sender){
		plugin.customReload();
		sender.sendMessage(MessagesUtils.getColoredMessage(plugin.prefix+" &aConfig reloaded!"));
	}


}
