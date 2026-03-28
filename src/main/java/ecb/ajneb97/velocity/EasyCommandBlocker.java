package ecb.ajneb97.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.managers.ConfigManager;
import ecb.ajneb97.core.managers.UpdateCheckerManager;
import ecb.ajneb97.core.model.internal.UpdateCheckerResult;
import ecb.ajneb97.velocity.listeners.PlayerListener;
import ecb.ajneb97.velocity.utils.PluginMessagingUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "easycommandblocker", name = "EasyCommandBlocker",
        version = "1.17.1", authors = {"Ajneb97"})
public class EasyCommandBlocker {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private CommandsManager commandsManager;
    private ConfigManager configManager;
    public String prefix = "<dark_gray>[<aqua>Easy<blue>CommandBlocker<dark_gray>]";
    private final PluginContainer container;

    private UpdateCheckerManager updateCheckerManager;

    @Inject
    public EasyCommandBlocker(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, PluginContainer container) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.container = container;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.configManager = new ConfigManager(dataDirectory,"velocity-config.yml","config.yml",true);
        this.configManager.registerConfig();
        this.configManager.checkMessagesUpdate();
        commandsManager = new CommandsManager(configManager.getConfig());

        server.getEventManager().register(this, new PlayerListener(this));
        CommandMeta meta = server.getCommandManager().metaBuilder("easycommandblocker")
                .aliases("ecb").build();
        server.getCommandManager().register(meta, new MainCommand(this));

        server.getChannelRegistrar().register(PluginMessagingUtils.IDENTIFIER);

        updateCheckerManager = new UpdateCheckerManager(container.getDescription().getVersion().orElse("Unknown"));
        updateMessage(updateCheckerManager.check());
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        // Check if the identifier matches first, no matter the source.
        if (!PluginMessagingUtils.IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());
    }

    public ProxyServer getServer(){
        return this.server;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public void customReload(){
        configManager.registerConfig();
        this.commandsManager.load(configManager.getConfig());
    }

    public void updateMessage(UpdateCheckerResult result){
        if(!result.isError()){
            String latestVersion = result.getLatestVersion();
            if(latestVersion != null){
                getServer().getConsoleCommandSource().sendMessage(MiniMessage.miniMessage().deserialize(prefix+" <red>There is a new version available. <yellow>(<gray>"+latestVersion+"<yellow>)"));
                getServer().getConsoleCommandSource().sendMessage(MiniMessage.miniMessage().deserialize(prefix+" <red>You can download it at: <white>https://modrinth.com/plugin/easy-command-blocker"));
            }
        }else{
            getServer().getConsoleCommandSource().sendMessage(MiniMessage.miniMessage().deserialize(prefix+" <red>Error while checking update."));
        }
    }
}
