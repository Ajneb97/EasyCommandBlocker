package ecb.ajneb97.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.managers.ConfigManager;
import ecb.ajneb97.velocity.listeners.PlayerListener;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "easycommandblocker", name = "EasyCommandBlocker",
        version = "1.7.2", authors = {"Ajneb97"})
public class EasyCommandBlocker {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private CommandsManager commandsManager;
    private ConfigManager configManager;
    public String prefix = "&8[&bEasy&9CommandBlocker&8]";

    @Inject
    public EasyCommandBlocker(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.configManager = new ConfigManager(dataDirectory,"velocity-config.yml","config.yml");
        this.configManager.registerConfig();
        this.configManager.checkMessagesUpdate();
        commandsManager = new CommandsManager(configManager.getConfig());

        server.getEventManager().register(this, new PlayerListener(this));
        CommandMeta meta = server.getCommandManager().metaBuilder("easycommandblocker")
                .aliases("ecb").build();
        server.getCommandManager().register(meta, new MainCommand(this));
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
}
