package ecb.ajneb97.spigot;

import ecb.ajneb97.spigot.listeners.PlayerListener;
import ecb.ajneb97.spigot.listeners.PlayerListenerNew;
import ecb.ajneb97.spigot.managers.BungeeMessagingManager;
import ecb.ajneb97.spigot.managers.CommandsManagerSpigot;
import ecb.ajneb97.spigot.managers.ProtocolLibManager;
import ecb.ajneb97.spigot.managers.ViaVersionManager;
import ecb.ajneb97.spigot.utils.MessagesUtils;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class EasyCommandBlocker extends JavaPlugin {

    public static String prefix = "&8[&bEasy&9CommandBlocker&8]";
    private PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();
    public String latestversion;
    private String configRoute;

    private CommandsManagerSpigot commandsManager;
    private ProtocolLibManager protocolLibManager;
    private ViaVersionManager viaVersionManager;
    private BungeeMessagingManager bungeeMessagingManager;

    public void onEnable(){
        registerConfig();
        registerCommands();
        registerEvents();
        bungeeMessagingManager = new BungeeMessagingManager(this);
        commandsManager = new CommandsManagerSpigot(this);
        protocolLibManager = new ProtocolLibManager(this);
        viaVersionManager = new ViaVersionManager(this);
        checkMessagesUpdate();

        Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eThanks for using my plugin!   &f~Ajneb97"));

        updateChecker();
    }

    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been disabled! &fVersion: "+version));
    }
    public void registerConfig(){
        File config = new File(this.getDataFolder(),"config.yml");
        configRoute = config.getPath();
        if(!config.exists()){
            this.getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    public void customReload(){
        reloadConfig();
        commandsManager.load();
    }

    public void registerCommands(){
        this.getCommand("ecb").setExecutor(new MainCommand(this));
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        if(!OtherUtils.serverIsLegacy()){
            pm.registerEvents(new PlayerListenerNew(this), this);
        }
    }

    public CommandsManagerSpigot getCommandsManager() {
        return commandsManager;
    }

    public ProtocolLibManager getProtocolLibManager() {
        return protocolLibManager;
    }

    public ViaVersionManager getViaVersionManager() {
        return viaVersionManager;
    }

    public BungeeMessagingManager getBungeeMessagingManager() {
        return bungeeMessagingManager;
    }

    public void updateChecker(){
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=101752").openConnection();
            int timed_out = 1250;
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);
            latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (latestversion.length() <= 7) {
                if(!version.equals(latestversion)){
                    Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage("&cThere is a new version available. &e(&7"+latestversion+"&e)"));
                    Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage("&cYou can download it at: &fhttps://www.spigotmc.org/resources/101752/"));
                }
            }
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &cError while checking update."));
        }
    }

    public void checkMessagesUpdate() {
        Path configFile = Paths.get(configRoute);
        try {
            String configText = new String(Files.readAllBytes(configFile));
            if(!configText.contains("custom_commands_actions:")){
                List<String> list = new ArrayList<String>();
                list.add("/ver");
                list.add("/version");
                list.add("/about");
                getConfig().set("custom_commands_actions.example1.commands", list);
                list = new ArrayList<String>();
                list.add("console_command: kick %player% You can't see the version of the server!");
                getConfig().set("custom_commands_actions.example1.actions", list);
                saveConfig();
                commandsManager.load();
            }

            if(!configText.contains("blocked_command_default_actions:")){
                List<String> list = new ArrayList<String>();
                list.add("message: &8[&b&lECB&8] &cYou don't have permissions to use that command.");
                list.add("playsound: BLOCK_NOTE_BLOCK_PLING;10;0.1");
                list.add("title: 20;40;20;&cWhat are you doing?;&7Don't use that command!");
                getConfig().set("blocked_command_default_actions", list);
                saveConfig();
                commandsManager.load();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
