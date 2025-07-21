package ecb.ajneb97.spigot;

import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.managers.ConfigManager;
import ecb.ajneb97.core.managers.UpdateCheckerManager;
import ecb.ajneb97.core.model.internal.UpdateCheckerResult;
import ecb.ajneb97.spigot.listeners.PlayerListener;
import ecb.ajneb97.spigot.listeners.PlayerListenerNew;
import ecb.ajneb97.spigot.managers.BungeeMessagingManager;
import ecb.ajneb97.spigot.managers.PacketEventsManager;
import ecb.ajneb97.spigot.managers.PacketManager;
import ecb.ajneb97.spigot.managers.ProtocolLibManager;
import ecb.ajneb97.spigot.managers.ViaVersionManager;
import ecb.ajneb97.spigot.utils.MessagesUtils;
import ecb.ajneb97.spigot.utils.OtherUtils;
import ecb.ajneb97.spigot.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyCommandBlocker extends JavaPlugin {

    public static String prefix = "&8[&bEasy&9CommandBlocker&8]";
    private PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();
    public static ServerVersion serverVersion;

    private PacketManager packetManager;
    private ViaVersionManager viaVersionManager;
    private BungeeMessagingManager bungeeMessagingManager;
    private CommandsManager commandsManager;
    private ConfigManager configManager;
    private UpdateCheckerManager updateCheckerManager;

    @Override
    public void onLoad() {
        // Initialize packet manager early for PacketEvents
        initializePacketManager();
    }

    public void onEnable(){
        setVersion();

        this.configManager = new ConfigManager(this.getDataFolder().toPath(),"config.yml","config.yml",false);
        this.configManager.registerConfig();
        this.configManager.checkMessagesUpdate();

        // Initialize packet manager if not already done in onLoad
        if(packetManager == null) {
            initializePacketManager();
        }

        commandsManager = new CommandsManager(configManager.getConfig());
        registerCommands();
        registerEvents();

        bungeeMessagingManager = new BungeeMessagingManager(this);
        viaVersionManager = new ViaVersionManager(this);

        // Log packet manager status
        if(packetManager != null && packetManager.isEnabled()) {
            Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &aPacket manager initialized: " + packetManager.getLibraryName()));
        } else {
            Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &cNo packet manager available - tab completion blocking disabled"));
        }

        Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eThanks for using my plugin!   &f~Ajneb97"));

        updateCheckerManager = new UpdateCheckerManager(version);
        updateMessage(updateCheckerManager.check());
    }

    public void onDisable(){
        // Close packet manager
        if(packetManager != null) {
            packetManager.terminate();
        }

        Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &eHas been disabled! &fVersion: "+version));
    }

    /**
     * Initialize the appropriate packet manager based on configuration
     */
    private void initializePacketManager() {
        String packetLibrary = "auto"; // default value
        
        // Try to get configuration if available
        if(configManager != null && configManager.getConfig() != null) {
            packetLibrary = configManager.getConfig().getString("packet_library", "auto");
        }
        
        PacketManager protocolLibManager = null;
        PacketManager packetEventsManager = null;
        
        // Initialize managers based on availability
        if(!"packetevents".equals(packetLibrary)) {
            try {
                protocolLibManager = new ProtocolLibManager(this);
            } catch (Exception e) {
                getLogger().info("ProtocolLib not available: " + e.getMessage());
            }
        }
        
        if(!"protocollib".equals(packetLibrary)) {
            try {
                packetEventsManager = new PacketEventsManager(this);
            } catch (Exception e) {
                getLogger().info("PacketEvents not available: " + e.getMessage());
            }
        }
        
        // Choose the appropriate manager
        switch(packetLibrary.toLowerCase()) {
            case "protocollib":
                if(protocolLibManager != null && protocolLibManager.isEnabled()) {
                    this.packetManager = protocolLibManager;
                } else {
                    getLogger().warning("ProtocolLib was requested but is not available");
                }
                break;
            case "packetevents":
                if(packetEventsManager != null && packetEventsManager.isEnabled()) {
                    this.packetManager = packetEventsManager;
                } else {
                    getLogger().warning("PacketEvents was requested but is not available");
                }
                break;
            case "auto":
            default:
                // Prefer ProtocolLib if available, fallback to PacketEvents
                if(protocolLibManager != null && protocolLibManager.isEnabled()) {
                    this.packetManager = protocolLibManager;
                } else if(packetEventsManager != null && packetEventsManager.isEnabled()) {
                    this.packetManager = packetEventsManager;
                } else {
                    getLogger().warning("Neither ProtocolLib nor PacketEvents is available for tab completion blocking");
                }
                break;
        }
    }

    public void setVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];

        switch(bukkitVersion){
            case "1.20.5":
            case "1.20.6":
                serverVersion = ServerVersion.v1_20_R4;
                break;
            case "1.21":
            case "1.21.1":
                serverVersion = ServerVersion.v1_21_R1;
                break;
            case "1.21.2":
            case "1.21.3":
                serverVersion = ServerVersion.v1_21_R2;
                break;
            case "1.21.4":
                serverVersion = ServerVersion.v1_21_R3;
                break;
            case "1.21.5":
                serverVersion = ServerVersion.v1_21_R4;
                break;
            case "1.21.6":
            case "1.21.7":
            case "1.21.8":
                serverVersion = ServerVersion.v1_21_R5;
                break;
            default:
                try{
                    serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
                }catch(Exception e){
                    serverVersion = ServerVersion.v1_21_R5;
                }
        }
    }

    public void customReload(){
        configManager.registerConfig();
        this.commandsManager.load(configManager.getConfig());
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

    /**
     * Get the active packet manager
     * @return the active packet manager (ProtocolLib or PacketEvents)
     */
    public PacketManager getPacketManager() {
        return packetManager;
    }

    /**
     * @deprecated Use getPacketManager() instead. This method is kept for backward compatibility.
     * @return the packet manager if it's PacketEventsManager, null otherwise
     */
    @Deprecated
    public PacketEventsManager getPacketEventsManager() {
        if(packetManager instanceof PacketEventsManager) {
            return (PacketEventsManager) packetManager;
        }
        return null;
    }

    public ViaVersionManager getViaVersionManager() {
        return viaVersionManager;
    }

    public BungeeMessagingManager getBungeeMessagingManager() {
        return bungeeMessagingManager;
    }

    public UpdateCheckerManager getUpdateCheckerManager() {
        return updateCheckerManager;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public void updateMessage(UpdateCheckerResult result){
        if(!result.isError()){
            String latestVersion = result.getLatestVersion();
            if(latestVersion != null){
                Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage("&cThere is a new version available. &e(&7"+latestVersion+"&e)"));
                Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage("&cYou can download it at: &fhttps://www.spigotmc.org/resources/101752/"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(MessagesUtils.getColoredMessage(prefix+" &cError while checking update."));
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}