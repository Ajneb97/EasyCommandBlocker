package ecb.ajneb97.waterfall;

import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.managers.ConfigManager;
import ecb.ajneb97.core.managers.UpdateCheckerManager;
import ecb.ajneb97.core.model.GlobalVariables;
import ecb.ajneb97.core.model.internal.UpdateCheckerResult;
import ecb.ajneb97.waterfall.listeners.PlayerListener;
import ecb.ajneb97.waterfall.utils.MessagesUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

public class EasyCommandBlocker extends Plugin {

    public String prefix = "&8[&bEasy&9CommandBlocker&8]";
    private PluginDescription pdfFile = getDescription();
    public String version = pdfFile.getVersion();

    private CommandsManager commandsManager;
    private ConfigManager configManager;
    private UpdateCheckerManager updateCheckerManager;

    public void onEnable(){
        /*
        if(!ProxyServer.getInstance().getVersion().contains("Waterfall")){
            ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &cThe plugin requires Waterfall or Velocity to work!"));
            return;
        }
         */

        getProxy().registerChannel(GlobalVariables.bungeeMainChannel);
        this.configManager = new ConfigManager(this.getDataFolder().toPath(),"bungee-config.yml","config.yml");
        this.configManager.registerConfig();
        this.configManager.checkMessagesUpdate();
        commandsManager = new CommandsManager(configManager.getConfig());
        registerCommands();
        registerEvents();

        ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been enabled! &fVersion: "+version));
        ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eThanks for using my plugin!   &f~Ajneb97"));

        updateCheckerManager = new UpdateCheckerManager(version);
        updateMessage(updateCheckerManager.check());
    }

    public void onDisable(){
        ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been disabled! &fVersion: "+version));
    }
    public void customReload(){
        configManager.registerConfig();
        this.commandsManager.load(configManager.getConfig());
    }

    public void registerCommands(){
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MainCommand(this));
    }

    public void registerEvents() {
        PluginManager pm = getProxy().getPluginManager();
        pm.registerListener(this, new PlayerListener(this));
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public void updateMessage(UpdateCheckerResult result){
        if(!result.isError()){
            String latestVersion = result.getLatestVersion();
            if(latestVersion != null){
                ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage("&cThere is a new version available. &e(&7"+latestVersion+"&e)"));
                ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage("&cYou can download it at: &fhttps://www.spigotmc.org/resources/101752/"));
            }
        }else{
            ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &cError while checking update."));
        }
    }


}
