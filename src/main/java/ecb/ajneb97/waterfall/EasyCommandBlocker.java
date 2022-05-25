package ecb.ajneb97.waterfall;

import ecb.ajneb97.core.model.GlobalVariables;
import ecb.ajneb97.waterfall.listeners.PlayerListener;
import ecb.ajneb97.waterfall.managers.CommandsManager;
import ecb.ajneb97.waterfall.utils.MessagesUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

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

public class EasyCommandBlocker extends Plugin {

    public String prefix = "&8[&bEasy&9CommandBlocker&8]";
    private Configuration config;
    private File configFile;

    private PluginDescription pdfFile = getDescription();
    public String version = pdfFile.getVersion();
    public String latestversion;
    private String configRoute;

    private CommandsManager commandsManager;

    public void onEnable(){
        if(!ProxyServer.getInstance().getVersion().contains("Waterfall")){
            ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &cThe plugin requires Waterfall to work!"));
            return;
        }

        getProxy().registerChannel(GlobalVariables.bungeeMainChannel);
        registerConfig();
        registerCommands();
        registerEvents();
        commandsManager = new CommandsManager(this);
        checkMessagesUpdate();

        ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been enabled! &fVersion: "+version));
        ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eThanks for using my plugin!   &f~Ajneb97"));

        updateChecker();
    }

    public void onDisable(){
        ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been disabled! &fVersion: "+version));
    }

    public void registerConfig() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdir();
        configFile = new File(this.getDataFolder(), "config.yml");
        try {
            configRoute = configFile.getPath();
            if (!configFile.exists())
                Files.copy(this.getResourceAsStream("bungee-config.yml"), configFile.toPath());

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return this.config;
    }

    public void customReload(){
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        commandsManager.load();
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
                    ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage("&cThere is a new version available. &e(&7"+latestversion+"&e)"));
                    ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage("&cYou can download it at: &fhttps://www.spigotmc.org/resources/101752/"));
                }
            }
        } catch (Exception ex) {
            ProxyServer.getInstance().getConsole().sendMessage(MessagesUtils.getColoredMessage(prefix+" &cError while checking update."));
        }
    }

    public void checkMessagesUpdate() {
        Path configFile = Paths.get(configRoute);
        try {
            String configText = new String(Files.readAllBytes(configFile));

            if(!configText.contains("custom_commands_actions:")){
                List<String> list = new ArrayList<String>();
                list.add("/bungee");
                getConfig().set("custom_commands_actions.example1.commands", list);
                list = new ArrayList<String>();
                list.add("message: &8[&b&lECB&8] &cYou can't see the BungeeCord version!");
                getConfig().set("custom_commands_actions.example1.actions", list);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
                commandsManager.load();
            }

            if(!configText.contains("blocked_command_default_actions:")){
                List<String> list = new ArrayList<String>();
                list.add("message: &8[&b&lECB&8] &cYou don't have permissions to use that command.");
                list.add("playsound: BLOCK_NOTE_BLOCK_PLING;10;0.1");
                list.add("title: 20;40;20;&cWhat are you doing?;&7Don't use that command!");
                getConfig().set("blocked_command_default_actions", list);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
                commandsManager.load();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
