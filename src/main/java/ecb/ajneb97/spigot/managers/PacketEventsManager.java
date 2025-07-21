package ecb.ajneb97.spigot.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PacketEventsManager implements PacketManager {
    private EasyCommandBlocker plugin;
    private boolean enabled;
    private HashMap<UUID, String> commandsWaiting = new HashMap<>();
    private TabCompleteListener tabCompleteListener;

    public PacketEventsManager(EasyCommandBlocker plugin) {
        this.plugin = plugin;
        this.enabled = false;

        // PacketEvents loads automatically, no plugin control needed
        try {
            // Check if PacketEvents API is ready
            if (PacketEvents.getAPI() != null) {
                this.enabled = true;
                plugin.getLogger().info("Using PacketEvents for packet management");

                // Register packet listener
                tabCompleteListener = new TabCompleteListener(PacketListenerPriority.HIGHEST);
                PacketEvents.getAPI().getEventManager().registerListener(tabCompleteListener);
            } else {
                plugin.getLogger().warning("PacketEvents API not available");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("PacketEvents could not be initialized: " + e.getMessage());
            this.enabled = false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getLibraryName() {
        return "PacketEvents";
    }

    private class TabCompleteListener extends PacketListenerAbstract {

        public TabCompleteListener(PacketListenerPriority priority) {
            super(priority);
        }

        @Override
        public void onPacketReceive(PacketReceiveEvent event) {
            if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
                handleClientTabComplete(event);
            }
        }

        @Override
        public void onPacketSend(PacketSendEvent event) {
            if (event.getPacketType() == PacketType.Play.Server.TAB_COMPLETE) {
                handleServerTabComplete(event);
            }
        }

        private void handleClientTabComplete(PacketReceiveEvent event) {
            if (event.isCancelled()) {
                return;
            }

            Player player = (Player) event.getPlayer();
            if (player == null) {
                return;
            }

            if (player.isOp() || player.hasPermission("easycommandblocker.bypass.tab")) {
                return;
            }

            WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
            String message = wrapper.getText();

            if (message == null || message.isEmpty()) {
                return;
            }

            boolean playerIsLegacy = plugin.getViaVersionManager().playerIsLegacy(player);

            if (OtherUtils.serverIsLegacy()) {
                if (playerIsLegacy) {
                    commandsWaiting.put(player.getUniqueId(), message);
                } else {
                    event.setCancelled(true);
                }
            } else {
                if (playerIsLegacy) {
                    event.setCancelled(true);
                }
            }
        }

        private void handleServerTabComplete(PacketSendEvent event) {
            Player player = (Player) event.getPlayer();
            if (player == null) {
                return;
            }

            boolean playerIsLegacy = plugin.getViaVersionManager().playerIsLegacy(player);
            if (!playerIsLegacy || !OtherUtils.serverIsLegacy()) {
                return;
            }

            if (player.isOp() || player.hasPermission("easycommandblocker.bypass.tab")) {
                return;
            }

            WrapperPlayServerTabComplete wrapper = new WrapperPlayServerTabComplete(event);

            String waitCommand = commandsWaiting.get(player.getUniqueId());
            commandsWaiting.remove(player.getUniqueId());

            if (waitCommand == null) {
                // Boş tamamlamalar
                event.setCancelled(true);
                return;
            }

            if (!waitCommand.startsWith("/")) {
                // Kullanıcı adı tamamlamalarını gönder
                return;
            }

            CommandsManager commandsManager = plugin.getCommandsManager();
            List<String> commands = commandsManager.getTabCommands(OtherUtils.getPlayerPermissionsList(player));

            if (commands == null) {
                return;
            }

            List<String> newSuggestions = new ArrayList<>();
            boolean isArgument = false;

            if (waitCommand.contains(" ")) {
                waitCommand = waitCommand.split(" ")[0];
                isArgument = true;
            }

            for (String command : commands) {
                command = command.split(" ")[0];
                if (!newSuggestions.contains(command) && command.startsWith(waitCommand)) {
                    if (isArgument) {
                        return;
                    }
                    newSuggestions.add(command);
                }
            }

            // Yeni komut eşleşmeleri oluştur
            List<WrapperPlayServerTabComplete.CommandMatch> commandMatches = new ArrayList<>();
            for (String suggestion : newSuggestions) {
                commandMatches.add(new WrapperPlayServerTabComplete.CommandMatch(suggestion, null));
            }
            wrapper.setCommandMatches(commandMatches);
        }
    }

    // Cleanup method called when plugin is disabled
    public void terminate() {
        if (enabled && tabCompleteListener != null) {
            try {
                // Remove listener
                PacketEvents.getAPI().getEventManager().unregisterListener(tabCompleteListener);
            } catch (Exception e) {
                // Continue silently on error
            }
        }
        commandsWaiting.clear();
    }
}